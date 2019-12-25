package net.simforge.scenery.desktop;

import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.desktop.model.InstalledScenery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstallationProcessor {

    public static void perform(InstallationService installationService, List<SceneryInfoDto> expectedSceneryList) throws IOException {
        List<InstalledScenery> installedSceneryList = installationService.getInstalledSceneryList();

        List<Operation> operations = new ArrayList<>();

        for (SceneryInfoDto scenery : expectedSceneryList) {
            InstalledScenery installedScenery = installedSceneryList.stream().filter(each -> each.getSceneryId().equals(scenery.getId())).findFirst().orElse(null);
            installedSceneryList.remove(installedScenery);

            boolean needToInstall = false;
            boolean needToUninstall = false;

            if (installedScenery == null) {

                needToInstall = true;

            } else {

                String installedRevision = installedScenery.getRevision();
                if (installedRevision.compareTo(scenery.getRevisionNumber()) < 0) {
                    needToInstall = true;
                    needToUninstall = true;
                }

            }

            if (needToUninstall) {
                operations.add(Operation.uninstall(installedScenery));
            }

            if (needToInstall) {
                operations.add(Operation.install(scenery));
            }
        }

        for (InstalledScenery installedScenery : installedSceneryList) {
            operations.add(Operation.uninstall(installedScenery));
        }

        for (Operation operation : operations) {
            operation.perform(installationService);
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
