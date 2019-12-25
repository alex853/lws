package net.simforge.scenery.web.dto;

@Deprecated
public class SceneryInfoDto {
    private String id;
    private String sceneryTitle;
    private String sceneryAuthors;
    private String revisionNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSceneryTitle() {
        return sceneryTitle;
    }

    public void setSceneryTitle(String sceneryTitle) {
        this.sceneryTitle = sceneryTitle;
    }

    public String getSceneryAuthors() {
        return sceneryAuthors;
    }

    public void setSceneryAuthors(String sceneryAuthors) {
        this.sceneryAuthors = sceneryAuthors;
    }

    public String getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(String revisionNumber) {
        this.revisionNumber = revisionNumber;
    }
}
