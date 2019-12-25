package net.simforge.scenery.core.dto;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryRevision;

import java.io.IOException;

public class SceneryInfoDto {
    private String id;
    private String sceneryTitle;
    private String sceneryDescription;
    private String sceneryAuthors;
    private String revisionNumber;
    private String revisionRepoPath;
    private String revisionDestPath;
    private String revisionInstallationSteps;

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

    public String getSceneryDescription() {
        return sceneryDescription;
    }

    public void setSceneryDescription(String sceneryDescription) {
        this.sceneryDescription = sceneryDescription;
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

    public String getRevisionRepoPath() {
        return revisionRepoPath;
    }

    public void setRevisionRepoPath(String revisionRepoPath) {
        this.revisionRepoPath = revisionRepoPath;
    }

    public String getRevisionDestPath() {
        return revisionDestPath;
    }

    public void setRevisionDestPath(String revisionDestPath) {
        this.revisionDestPath = revisionDestPath;
    }

    public String getRevisionInstallationSteps() {
        return revisionInstallationSteps;
    }

    public void setRevisionInstallationSteps(String revisionInstallationSteps) {
        this.revisionInstallationSteps = revisionInstallationSteps;
    }

    public static SceneryInfoDto readScenery(JsonReader jsonReader) throws IOException {
        SceneryInfoDto scenery = new SceneryInfoDto();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "id":
                    scenery.setId(readString(jsonReader));
                    break;
                case "sceneryTitle":
                    scenery.setSceneryTitle(readString(jsonReader));
                    break;
                case "sceneryDescription":
                    scenery.setSceneryDescription(readString(jsonReader));
                    break;
                case "sceneryAuthors":
                    scenery.setSceneryAuthors(readString(jsonReader));
                    break;
/*                case "icao":
                    scenery.setIcao(readString(jsonReader));
                    break;*/
                case "revisionNumber":
                    scenery.setRevisionNumber(readString(jsonReader));
                    break;
                case "revisionRepoPath":
                    scenery.setRevisionRepoPath(readString(jsonReader));
                    break;
                case "revisionDestPath":
                    scenery.setRevisionDestPath(readString(jsonReader));
                    break;
                case "revisionInstallationSteps":
                    scenery.setRevisionInstallationSteps(readString(jsonReader));
                    break;
            }
        }
        jsonReader.endObject();
        return scenery;
    }

    private static String readString(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.NULL) {
            return jsonReader.nextString();
        } else {
            jsonReader.skipValue();
            return null;
        }
    }

    public static SceneryInfoDto toDto(Scenery scenery, SceneryRevision revision) {
        SceneryInfoDto dto = new SceneryInfoDto();

        dto.setId(String.valueOf(scenery.getId()));
        dto.setSceneryTitle(scenery.getTitle());
        dto.setSceneryDescription(scenery.getDescription());
        dto.setSceneryAuthors(scenery.getAuthors());

        dto.setRevisionNumber(String.valueOf(revision.getRevNumber()));
        dto.setRevisionRepoPath(revision.getRepoPath());
        dto.setRevisionDestPath(revision.getDestPath());
        dto.setRevisionInstallationSteps(revision.getInstallationSteps());

        return dto;
    }

}
