package net.simforge.scenery.desktop;

import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.desktop.model.InstalledScenery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Installer {

    private static final Logger logger = LoggerFactory.getLogger(Installer.class.getName());

    public static void main(String[] args) {
        try {
            String action = "install";
            if (args.length == 1) {
                action = args[0];
            }
            logger.info("Selected action is '{}'", action);

            Configuration cfg = Configuration.load();
            logger.info("Configuration loaded");

            RestClient restClient = new RestClient(cfg);
            InstallationService installationService = new InstallationService(cfg, restClient);

            List<SceneryInfoDto> expectedSceneryList;

            if ("install".equalsIgnoreCase(action)) {
                List<SceneryInfoDto> sceneryList = restClient.loadSceneryList();
                logger.info("Scenery list loaded - there are {} sceneries available", sceneryList.size());

                expectedSceneryList = sceneryList;
            } else if ("uninstall".equalsIgnoreCase(action)) {
                expectedSceneryList = new ArrayList<>();
                logger.info("All sceneries will be uninstalled");
            } else {
                logger.warn("Unknown action '{}', exiting", action);
                return;
            }

            List<InstalledScenery> installedSceneryList = installationService.getInstalledSceneryList();
            logger.info("There are {} sceneries installed at the moment", installedSceneryList.size());

            List<Operation> operations = new ArrayList<>();

            for (SceneryInfoDto scenery : expectedSceneryList) {
                InstalledScenery installedScenery = installedSceneryList.stream().filter(each -> each.getSceneryId().equals(scenery.getId())).findFirst().orElse(null);
                installedSceneryList.remove(installedScenery);

                if (installedScenery == null) {
                    operations.add(Operation.install(scenery));
                    logger.info("    Scenery {} - INSTALL queued", scenery.getSceneryTitle());
                } else {
                    String installedRevision = installedScenery.getRevision();
                    if (installedRevision.compareTo(scenery.getRevisionNumber()) < 0) {
                        operations.add(Operation.uninstall(installedScenery));
                        operations.add(Operation.install(scenery));
                        logger.info("    Scenery {} - UPDATE (uninstall&install) queued", scenery.getSceneryTitle());
                    }
                }
            }

            for (InstalledScenery installedScenery : installedSceneryList) {
                operations.add(Operation.uninstall(installedScenery));
                logger.info("    Scenery {} - UNINSTALL queued", installedScenery.getSceneryCfgLocal()); // todo add scenery title
            }

            logger.info("There are {} operations queued", operations.size());
            int counter = 0;
            for (Operation operation : operations) {
                operation.perform(installationService);
                logger.info("    {} of {} done", ++counter, operations.size());
            }
            logger.info("Finished successfully");
        } catch (IOException e) {
            logger.error("IO error", e);
        }
    }

    private abstract static class Operation {

        public abstract void perform(InstallationService installationService) throws IOException;

        public static Operation install(SceneryInfoDto scenery) {
            return new Operation() {
                @Override
                public void perform(InstallationService installationService) throws IOException {
                    installationService.installScenery(scenery);
                }
            };
        }

        public static Operation uninstall(InstalledScenery installedScenery) {
            return new Operation() {
                @Override
                public void perform(InstallationService installationService) throws IOException {
                    installationService.uninstallScenery(installedScenery);
                }
            };
        }

    }


}
