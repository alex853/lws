package net.simforge.scenery.desktop;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.simforge.commons.legacy.BM;
import net.simforge.scenery.core.Steps;
import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.desktop.model.InstalledScenery;
import net.simforge.scenery.core.dto.StepDto;
import net.simforge.scenery.desktop.model.SceneryCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InstallationService {

    private static final Logger logger = LoggerFactory.getLogger(InstallationService.class.getName());

    private Configuration cfg;
    private RestClient restClient;

    public InstallationService(Configuration cfg, RestClient restFacade) {
        this.cfg = cfg;
        this.restClient = restFacade;
    }

    public List<InstalledScenery> getInstalledSceneryList() throws IOException {
        BM.start("InstallationService.getInstalledSceneryList");
        try {
            List<InstalledScenery> installedSceneryList = null;
            String installedSceneryFile = getInstalledSceneryFile();

            if (new File(installedSceneryFile).exists()) {
                logger.debug("Scenery status file - loading from {}", installedSceneryFile);
                try (FileReader reader = new FileReader(installedSceneryFile)) {
                    installedSceneryList = new Gson()
                            .fromJson(reader,
                                    new TypeToken<List<InstalledScenery>>() {
                                    }.getType());
                    logger.info("Scenery statis file - loaded from {}", installedSceneryFile);
                }
            }

            if (installedSceneryList == null) {
                logger.debug("Scenery status file - using empty");
                installedSceneryList = new ArrayList<>();
            }

            return installedSceneryList;
        } finally {
            BM.stop();
        }
    }

    public void installScenery(SceneryInfoDto scenery) throws IOException {
        BM.start("InstallationService.installScenery");
        try {
            logger.debug("Installing scenery '{}' (rev {}, repo-path {})",
                    scenery.getSceneryTitle(),
                    scenery.getRevisionNumber(),
                    scenery.getRevisionRepoPath());

            String localStore = getLocalStoreFolder();

            if (scenery.getRevisionInstallationSteps() == null) {
                throw new IllegalArgumentException("No installation steps for scenery, unable to install scenery");
            }

            List<StepDto> steps = new Gson().fromJson(
                    scenery.getRevisionInstallationSteps(),
                    new TypeToken<List<StepDto>>() {
                    }.getType());
            logger.debug("Installation steps parsed - {} steps", steps.size());

            Map<String, String> stepFile_2_localFile = new HashMap<>();
            for (StepDto step : steps) {
                String stepFile = step.getFile();
                String localFile;
                if (Steps.PACKAGE.equals(stepFile)) {
                    localFile = localStore + "/" + scenery.getRevisionRepoPath().replace('/', '_') + "-" + System.currentTimeMillis() + ".package.zip";
                } else {
                    localFile = localStore + "/" + scenery.getRevisionRepoPath().replace('/', '_') + "-" + System.currentTimeMillis() + "." + stepFile;
                }
                stepFile_2_localFile.put(stepFile, localFile);
            }
            logger.debug("Files to load: {}", Joiner.on(", ").join(stepFile_2_localFile.keySet()));

            // todo scenery.getRepoMode();

            for (Map.Entry<String, String> entry : stepFile_2_localFile.entrySet()) {
                String stepFile = entry.getKey();
                String localFile = entry.getValue();

                logger.debug("Loading archive {} to file {}", stepFile, localFile);
                if (Steps.PACKAGE.equals(stepFile)) {
                    restClient.downloadPackage(scenery, localFile);
                } else {
                    restClient.downloadArchive(scenery, stepFile, localFile);
                }
            }

            Map<String, String> context = new HashMap<>();
            context.put(Steps.FSX_ROOT, cfg.getFsPath());
            context.put(Steps.ADDON_SCENERY, cfg.getAddonSceneryPath());
            context.put(Steps.FSX_EFFECTS, cfg.getFsPath() + "/Effects");
            context.put(Steps.REPO_PATH, scenery.getRevisionRepoPath());
            context.put(Steps.DEST_PATH, scenery.getRevisionDestPath());
            // todo add kind of group name

            List<String> files = new ArrayList<>();
            for (StepDto step : steps) {
                String stepFile = step.getFile();
                String destFile = stepFile_2_localFile.get(stepFile);

                logger.debug("Doing {}", step);
                int copied = 0;
                int skipped = 0;

                try (FileInputStream fileInputStream = new FileInputStream(destFile)) {
                    ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        String file = entry.getName();

                        String matchedFilename = Steps.Source.getMatchedFilename(step.getSource(), file);
                        if (matchedFilename == null) {
                            logger.trace("File {} skipped due no-match", file);
                            skipped++;
                            continue;
                        }

                        String destFilename = makeDestFilename(context, step.getDest(), matchedFilename);

                        if (!files.contains(destFilename)) {
                            files.add(destFilename);
                        }

                        logger.trace("File {} goes to {}", file, destFilename);

                        Path destPath = Paths.get(destFilename);
                        Files.createDirectories(destPath.getParent());

                        Files.copy(zipInputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
                        copied++;
                    }
                }

                logger.debug("Step stats: copied {} files, skipped {} files", copied, skipped);
            }

            for (Map.Entry<String, String> entry : stepFile_2_localFile.entrySet()) {
                String destFile = entry.getValue();

                new File(destFile).deleteOnExit();
            }



            String dest = scenery.getRevisionDestPath();
            String local = null;
            if (dest != null && !"%ADDON_SCENERY%/scenery".equalsIgnoreCase(dest)) { // todo more wise condition for "%ADDON_SCENERY%/scenery" to prevent creation of areas for sceneries being put into addon scenery
                local = dest
                        .replaceAll(Steps.ADDON_SCENERY, "Addon Scenery")
                        .replaceAll(Steps.REPO_PATH, scenery.getRevisionRepoPath())
                        .replace('/', '\\');
            }

            if (local != null) {
                SceneryCfg sceneryCfg = SceneryCfg.load(cfg.getSceneryCfgPath());

                SceneryCfg.Area area = sceneryCfg.getAreaByLocal(local);
                if (area == null) {
                    area = sceneryCfg.append();
                }
                area.setTitle("[LWS] " + scenery.getSceneryTitle());
                area.setLocal(local);
                area.setActive(true);
                area.setRequired(false);

                sceneryCfg.save(cfg.getSceneryCfgPath());
            }



            InstalledScenery installedScenery = new InstalledScenery();
            installedScenery.setSceneryId(scenery.getId());
            installedScenery.setRevision(scenery.getRevisionNumber());
            installedScenery.setSceneryCfgLocal(local);
            installedScenery.setFiles(files);

            List<InstalledScenery> installedSceneryList = getInstalledSceneryList();
            installedSceneryList.add(installedScenery);
            _saveInstalledSceneryList(installedSceneryList);

            logger.info("Scenery '{}' (rev {}, repo-path {}) INSTALLED",
                    scenery.getSceneryTitle(),
                    scenery.getRevisionNumber(),
                    scenery.getRevisionRepoPath());
        } finally {
            BM.stop();
        }
    }

    private String getLocalStoreFolder() {
        String workFolder = cfg.getLocalStorePath();
        new File(workFolder).mkdirs();
        return workFolder;
    }

    private String makeDestFilename(Map<String, String> context, String dest, String filename) {
        String result = dest;
        for (Map.Entry<String, String> entry : context.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        result = result + "/" + filename;
        return result;
    }

    public void uninstallScenery(final InstalledScenery _installedScenery) throws IOException {
        BM.start("InstallationService.uninstallScenery");
        try {
            logger.debug("Uninstalling scenery '{}' (rev {})",
                    "TODO TITLE & REPOPATH", // todo
                    _installedScenery.getRevision());

            List<InstalledScenery> installedSceneryList = getInstalledSceneryList();
            InstalledScenery installedScenery = installedSceneryList.stream().filter(each -> each.getSceneryId().equals(_installedScenery.getSceneryId())).findFirst().orElse(null);
            if (installedScenery == null) {
                throw new IllegalArgumentException("Can't find installed scenery");
            }

            List<String> files = installedScenery.getFiles();
            logger.debug("There are {} files for deletion", files.size());

            Set<Path> folders = new TreeSet<>();
            for (String file : files) {
                Path path = Paths.get(file);
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                logger.trace("File {} deleted", file);

                folders.add(path.getParent());
            }

            logger.debug("There are {} folders for deletion", folders.size());
            for (Path _folder : folders) {
                File folder = _folder.toFile();
                while (true) {
                    if (!folder.exists()) {
                        logger.trace("Folder {} does not exist", folder);
                        break;
                    }

                    boolean isFSXFolder = isFSXFolder(folder); // any of FSX folder - Addon Scenery, or Addon Scenery/scenery, etc
                    if (isFSXFolder) {
                        logger.trace("Folder {} relates to sim, stopping", folder);
                        break;
                    }

                    boolean filesArePresented = folder.list().length != 0;
                    if (!filesArePresented) {
                        folder.delete();
                        logger.trace("Folder {} deleted", folder);
                    } else {
                        logger.trace("Folder {} is not empty, stopping", folder);
                    }

                    folder = folder.getParentFile();
                }
            }



            if (installedScenery.getSceneryCfgLocal() != null) {
                SceneryCfg sceneryCfg = SceneryCfg.load(cfg.getSceneryCfgPath());

                SceneryCfg.Area area = sceneryCfg.getAreaByLocal(installedScenery.getSceneryCfgLocal());
                if (area != null) {
                    sceneryCfg.remove(area);
                    sceneryCfg.save(cfg.getSceneryCfgPath());
                }
            }



            installedSceneryList.remove(installedScenery);
            _saveInstalledSceneryList(installedSceneryList);

            logger.info("Scenery '{}' (rev {}) UNINSTALLED",
                    "TODO TITLE & REPOPATH", // todo
                    _installedScenery.getRevision());
        } finally {
            BM.stop();
        }
    }

    private boolean isFSXFolder(File folder) {
        String lastName = folder.getName();// getName(folder.getNameCount() - 1).toString();
        String prevName = folder.getParentFile().getName();// getNameCount() > 1 ? folder.getName(folder.getNameCount() - 2).toString() : null;

        if ("Addon Scenery".equalsIgnoreCase(lastName)) {
            return true;
        }

        if ("Scenery".equalsIgnoreCase(lastName)) {
            return "Addon Scenery".equalsIgnoreCase(prevName);
        }

        if ("Texture".equalsIgnoreCase(lastName)) {
            return "Addon Scenery".equalsIgnoreCase(prevName);
        }

        if ("Effects".equalsIgnoreCase(lastName)) {
            return true;
        }

        return false;
    }

    private void _saveInstalledSceneryList(List<InstalledScenery> installedSceneryList) throws IOException {
        BM.start("InstallationService._saveInstalledSceneryList");
        try {
            String installedSceneryFile = getInstalledSceneryFile();

            try (FileWriter writer = new FileWriter(installedSceneryFile)) {
                new Gson().toJson(installedSceneryList, writer);
            }

            logger.info("Scenery status file - saved to {}", installedSceneryFile);
        } finally {
            BM.stop();
        }
    }

    private String getInstalledSceneryFile() {
        return new File(cfg.getSceneryCfgPath()).getParent().toString() + "/lws-installed.json";
    }
}
