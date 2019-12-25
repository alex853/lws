package net.simforge.scenery.desktop;

import net.simforge.commons.io.IOHelper;
import net.simforge.scenery.TestData;
import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.core.persistence.SceneryRevision;
import net.simforge.scenery.core.service.SimpleRepositoryService;
import net.simforge.scenery.desktop.model.InstalledScenery;
import net.simforge.scenery.desktop.model.SceneryCfg;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class InstallationServiceTest {

    private String testFolder;
    private String fsxRoot;
    private String sceneryCfgPath;
    private String repoRoot;
    private String addonSceneryPath;

    private Configuration cfg;

    private RestClient restClient;
    private InstallationService installationService;

    @Before
    public void before() throws IOException {
        testFolder = "./temp/" + System.currentTimeMillis();
        fsxRoot = testFolder + "/FSX";
        addonSceneryPath = fsxRoot + "/Addon Scenery";
        sceneryCfgPath = testFolder + "/Scenery.CFG";

        Files.createDirectories(Paths.get(fsxRoot));
        Files.createDirectories(Paths.get(addonSceneryPath));
        Files.createDirectories(Paths.get(addonSceneryPath, "scenery"));
        Files.createDirectories(Paths.get(addonSceneryPath, "texture"));
        Files.createDirectories(Paths.get(fsxRoot, "Effects"));

        repoRoot = TestData.getTestDataRepo();

        Files.copy(
                Paths.get(IOHelper.resourceToPath(SceneryCfgTest.class, "scenery.fsx-original.cfg")),
                Paths.get(sceneryCfgPath));

        cfg = Mockito.mock(Configuration.class);
        Mockito.when(cfg.getFsPath()).thenReturn(fsxRoot);
        Mockito.when(cfg.getSceneryCfgPath()).thenReturn(sceneryCfgPath);
        Mockito.when(cfg.getAddonSceneryPath()).thenReturn(addonSceneryPath);
        Mockito.when(cfg.getLocalSettingsPath()).thenReturn(testFolder);

        restClient = Mockito.mock(RestClient.class);

        Mockito.doAnswer((Answer<Void>) invocation -> {
            SceneryInfoDto scenery = invocation.getArgument(0);
            String destFilename = invocation.getArgument(1);

            SimpleRepositoryService repositoryService = new SimpleRepositoryService(repoRoot);
            SceneryRevision revision = Mockito.mock(SceneryRevision.class);
            Mockito.when(revision.getRepoPath()).thenReturn(scenery.getRevisionRepoPath());
            Mockito.when(revision.getRevNumber()).thenReturn(Integer.parseInt(scenery.getRevisionNumber()));

            try (FileOutputStream fos = new FileOutputStream(destFilename)) {
                repositoryService.loadPackage(revision, fos);
            }

            return null;
        }).when(restClient).downloadPackage(any(), any());

        Mockito.doAnswer((Answer<Void>) invocation -> {
            SceneryInfoDto scenery = invocation.getArgument(0);
            String archiveName = invocation.getArgument(1);
            String destFilename = invocation.getArgument(2);

            SimpleRepositoryService repositoryService = new SimpleRepositoryService(repoRoot);
            SceneryRevision revision = Mockito.mock(SceneryRevision.class);
            Mockito.when(revision.getRepoPath()).thenReturn(scenery.getRevisionRepoPath());
            Mockito.when(revision.getRevNumber()).thenReturn(Integer.parseInt(scenery.getRevisionNumber()));

            try (FileOutputStream fos = new FileOutputStream(destFilename)) {
                repositoryService.loadArchive(revision, archiveName, fos);
            }

            return null;
        }).when(restClient).downloadArchive(any(), any(), any());

        installationService = new InstallationService(cfg, restClient);
    }

    @After
    public void after() throws IOException {
        Files.walkFileTree(Paths.get(testFolder), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    public void testEdli() throws IOException {
        assertFSXStructure();
        assertEdliInstalled(false);

        SceneryInfoDto edli = SceneryInfoDto.toDto(TestData.edli, TestData.edliRevision);

        installationService.installScenery(edli);

        assertFSXStructure();
        assertEdliInstalled(true);

        installationService.uninstallScenery(findInstalled(edli));

        assertFSXStructure();
        assertEdliInstalled(false);
    }

    @Test
    public void testEdliEgsjLiml() throws IOException {
        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);

        SceneryInfoDto edli = SceneryInfoDto.toDto(TestData.edli, TestData.edliRevision);
        SceneryInfoDto egsj = SceneryInfoDto.toDto(TestData.egsj, TestData.egsjRevision);
        SceneryInfoDto liml = SceneryInfoDto.toDto(TestData.liml, TestData.limlRevision);

        installationService.installScenery(edli);
        installationService.installScenery(egsj);
        installationService.installScenery(liml);

        assertFSXStructure();
        assertEdliInstalled(true);
        assertEgsjInstalled(true);
        assertLimlInstalled(true);

        installationService.uninstallScenery(findInstalled(edli));
        installationService.uninstallScenery(findInstalled(egsj));
        installationService.uninstallScenery(findInstalled(liml));

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
    }

    @Test
    public void testEgll() throws IOException {
        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);

        SceneryInfoDto egll = SceneryInfoDto.toDto(TestData.egll, TestData.egllRevision);

        installationService.installScenery(egll);

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(true);

        installationService.uninstallScenery(findInstalled(egll));

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
    }

    @Test
    public void testLcphMK() throws IOException {
        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(false);

        SceneryInfoDto lcph = SceneryInfoDto.toDto(TestData.lcphMK, TestData.lcphMKRevision);

        installationService.installScenery(lcph);

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(true);

        installationService.uninstallScenery(findInstalled(lcph));

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(false);
    }

    @Test
    public void testEgph25A() throws IOException {
        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(false);
        assertEgph25AInstalled(false);

        SceneryInfoDto egph = SceneryInfoDto.toDto(TestData.egph25A, TestData.egph25ARevision);

        installationService.installScenery(egph);

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(false);
        assertEgph25AInstalled(true);

        installationService.uninstallScenery(findInstalled(egph));

        assertFSXStructure();
        assertEdliInstalled(false);
        assertEgsjInstalled(false);
        assertLimlInstalled(false);
        assertEgllInstalled(false);
        assertLcphMKInstalled(false);
        assertEgph25AInstalled(false);
    }

    private void assertFSXStructure() {
        assertTrue(Files.exists(Paths.get(fsxRoot)));
        assertTrue(Files.exists(Paths.get(addonSceneryPath)));
        assertTrue(Files.exists(Paths.get(addonSceneryPath, "scenery")));
        assertTrue(Files.exists(Paths.get(addonSceneryPath, "texture")));
        assertTrue(Files.exists(Paths.get(fsxRoot, "Effects")));
    }

    private void assertEdliInstalled(boolean expectedInstalled) throws IOException {
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EDLI-1710.bgl").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EDLI-1710_CVX.bgl").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EDLI-1710_OBJ.bgl").exists());

        // no folders check as the scenery goes into Addon Scenery

        assertEquals(expectedInstalled, testSceneryJson(TestData.edli.getId()));
    }

    private void assertEgsjInstalled(boolean expectedInstalled) throws IOException {
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EGSJ-1710.bgl").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EGSJ-1710_CVX.bgl").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/EGSJ-1710_OBJ.bgl").exists());

        // no folders check as the scenery goes into Addon Scenery

        assertEquals(expectedInstalled, testSceneryJson(TestData.egsj.getId()));
    }

    private void assertLimlInstalled(boolean expectedInstalled) throws IOException {
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/LIML_1710-1.bgl").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, "scenery/LIML_1710-1_OBJ.bgl").exists());

        // no folders check as the scenery goes into Addon Scenery

        assertEquals(expectedInstalled, testSceneryJson(TestData.liml.getId()));
    }

    private void assertEgllInstalled(boolean expectedInstalled) throws IOException {
        assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.egllRevision.getRepoPath() + "/scenery/EGLL_ADE_RS.BGL").exists());

        // folders check
        // todo uncomment it assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.egllRevision.getRepoPath()).exists());

        assertEquals(expectedInstalled, testSceneryJson(TestData.egll.getId()));

        assertEquals(expectedInstalled, testSceneryCfg("Addon Scenery\\EGLL"));
    }

    private void assertLcphMKInstalled(boolean expectedInstalled) throws IOException {
        // only few files checked here
        assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath() + "/scenery/000_LCPH_FSX.BGL").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath() + "/scenery/LCPH_ADEX_XX.BGL").exists());
        assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath() + "/scenery/14_LCPH_lines.bgl").exists());
        if (expectedInstalled) {
            assertEquals(868384, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath() + "/scenery/14_LCPH_lines.bgl").length());
        }
        assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath() + "/texture/c172_r.bmp").exists());
        assertEquals(expectedInstalled, new File(fsxRoot + "/Effects", "Cntrl_LCPH_TaxiGreen.fx").exists());
        assertEquals(expectedInstalled, new File(fsxRoot + "/Effects", "fx_LCPH_TaxiGreen.fx").exists());

        // folders check
        // todo uncomment it assertEquals(expectedInstalled, new File(addonSceneryPath, TestData.lcphMKRevision.getRepoPath()).exists());

        assertEquals(expectedInstalled, testSceneryJson(TestData.lcphMK.getId()));

        assertEquals(expectedInstalled, testSceneryCfg("Addon Scenery\\LCPH_MaxKraus"));
    }

    private void assertEgph25AInstalled(boolean expectedInstalled) throws IOException {
        // only few files checked here
        assertEquals(expectedInstalled, new File(addonSceneryPath, "EGPH_25A/scenery/EGPH_ADEX_25A.bgl").exists());

        assertEquals(expectedInstalled, testSceneryJson(TestData.egph25A.getId()));

        assertEquals(expectedInstalled, testSceneryCfg("Addon Scenery\\EGPH_25A"));
    }

    private InstalledScenery findInstalled(SceneryInfoDto sceneryInfoDto) throws IOException {
        List<InstalledScenery> installedSceneryList = installationService.getInstalledSceneryList();
        return installedSceneryList.stream().filter(installed -> installed.getSceneryId().equals(sceneryInfoDto.getId())).findFirst().orElse(null);
    }

    private boolean testSceneryJson(Integer sceneryId) throws IOException {
        List<InstalledScenery> installedSceneryList = installationService.getInstalledSceneryList();
        InstalledScenery installedScenery = installedSceneryList.stream().filter(each -> each.getSceneryId().equals(String.valueOf(sceneryId))).findFirst().orElse(null);
        return installedScenery != null;
    }

    private boolean testSceneryCfg(String local) throws IOException {
        SceneryCfg sceneryCfg = SceneryCfg.load(sceneryCfgPath);
        SceneryCfg.Area area = sceneryCfg.getAreaByLocal(local);
        return area != null;
    }

}
