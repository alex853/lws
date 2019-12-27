package net.simforge.scenery.desktop;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private String fsPath;
    private String sceneryCfgPath;
    private String localStorePath;
    private String desktopClientServiceUrl;

    public static Configuration load() throws IOException {
        Configuration cfg = new Configuration();

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("./lws.properties")) {
            properties.load(fis);
        }

        cfg.localStorePath = "./work";
        cfg.fsPath = (String) properties.get("fsPath");
        cfg.sceneryCfgPath = (String) properties.get("sceneryCfgPath");
        cfg.desktopClientServiceUrl = (String) properties.get("desktopClientServiceUrl");

        return cfg;
    }

    public String getFsPath() {
        return fsPath;
    }

    public String getSceneryCfgPath() {
        return sceneryCfgPath;
    }

    public String getAddonSceneryPath() {
        return fsPath + "/Addon Scenery";
    }

    public String getLocalStorePath() {
        return localStorePath;
    }

    public String getDesktopClientServiceUrl() {
        return desktopClientServiceUrl;
    }
}
