package net.simforge.scenery.core;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import net.simforge.scenery.core.dto.StepDto;

import java.util.ArrayList;
import java.util.List;

public class Steps {
    public static final String PACKAGE = "PACKAGE";
    public static final String SCENERY_TEXTURE = "%SCENERY&TEXTURE%";
    public static final String ADDON_SCENERY = "%ADDON_SCENERY%";
    public static final String REPO_PATH = "%REPO_PATH%";
    public static final String OWN_FOLDER = ADDON_SCENERY + "/" + REPO_PATH;
    public static final String FSX_ROOT = "%FSX%";
    public static final String FSX_EFFECTS = FSX_ROOT + "/Effects";

    public static Steps newSteps() {
        return new Steps();
    }

    private List<StepDto> steps = new ArrayList<>();

    public Steps addStep(String file, String source, String dest) {
        StepDto step = new StepDto();
        step.setFile(file);
        step.setSource(source);
        step.setDest(dest);
        steps.add(step);
        return this;
    }

    public String toJson() {
        return (new Gson()).toJson(steps);
    }

    public static class Source {
        public static boolean isMatch(String source, String filename) {
            return getMatchedFilename(source, filename) != null;
        }

        public static String getMatchedFilename(String source, String filename) {
            String[] sourceParts = source.split("[\\\\/]");
            String[] filenameParts = filename.split("[\\\\/]");

            List<String> matchedParts = new ArrayList<>();

            for (int i = 0; i < sourceParts.length; i++) {
                String sourcePart = sourceParts[i];

                if (sourcePart.equals(SCENERY_TEXTURE)) {
                    if (!hasFilenamePart(filenameParts, i)) {
                        return null;
                    }
                    String filenamePart = filenameParts[i];

                    if (filenamePart.equalsIgnoreCase("scenery")
                            || filenamePart.equalsIgnoreCase("texture")) {
                        // if filename contains scenery/somefile.txt then ok
                        // if filename contains scenery/somefolder/somefile.txt then fail
                        if (noMoreFilenameParts(filenameParts, i+1)) {
                            matchedParts.add(filenamePart);
                            matchedParts.add(filenameParts[i+1]);
                            return Joiner.on('/').join(matchedParts);
                        } else {
                            return null;
                        }
                    }
                } else if (sourcePart.contains("*")) {
                    String regexp = sourcePart.toLowerCase().replaceAll("[.]", "[.]");
                    regexp = regexp.replaceAll("[*]", ".*");

                    if (!hasFilenamePart(filenameParts, i)) {
                        return null;
                    }
                    String filenamePart = filenameParts[i];

                    if (filenamePart.toLowerCase().matches(regexp) && noMoreFilenameParts(filenameParts, i)) {
                        matchedParts.add(filenamePart);
                        return Joiner.on('/').join(matchedParts);
                    } else {
                        return null;
                    }
                } else {
                    if (!hasFilenamePart(filenameParts, i)) {
                        return null;
                    }

                    String filenamePart = filenameParts[i];
                    if (!filenamePart.equalsIgnoreCase(sourcePart)) {
                        return null;
                    }
                }
            }

            return null;
        }

        private static boolean hasFilenamePart(String[] filenameParts, int index) {
            return index < filenameParts.length;
        }

        private static boolean noMoreFilenameParts(String[] filenameParts, int currentIndex) {
            return filenameParts.length == currentIndex + 1;
        }
    }

    public static Path path(String... components) {
        return new Path(components);
    }

    public static class Path {
        private String[] components;

        public Path(String[] components) {
            this.components = components;
        }

        @Override
        public String toString() {
            return Joiner.on("/").join(components);
        }
    }
}
