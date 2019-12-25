package net.simforge.scenery.desktop.model;

import java.util.List;

public class InstalledScenery {
    private String sceneryId;
    private String revision;
    private String sceneryCfgLocal;
    private List<String> files;

    public String getSceneryId() {
        return sceneryId;
    }

    public void setSceneryId(String sceneryId) {
        this.sceneryId = sceneryId;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getSceneryCfgLocal() {
        return sceneryCfgLocal;
    }

    public void setSceneryCfgLocal(String sceneryCfgLocal) {
        this.sceneryCfgLocal = sceneryCfgLocal;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
