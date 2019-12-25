package net.simforge.scenery.desktop;

import net.simforge.scenery.core.dto.SceneryInfoDto;

import java.io.IOException;
import java.util.List;

public class InstallAll {
    public static void main(String[] args) throws IOException {

        Configuration cfg = Configuration.load();

        RestClient restClient = new RestClient(cfg);

        List<SceneryInfoDto> sceneryList = restClient.loadSceneryList();

        InstallationService installationService = new InstallationService(cfg, restClient);

        InstallationProcessor.perform(installationService, sceneryList);

    }
}
