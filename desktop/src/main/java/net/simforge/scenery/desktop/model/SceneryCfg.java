package net.simforge.scenery.desktop.model;

import net.simforge.commons.misc.Misc;
import net.simforge.commons.misc.Str;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class SceneryCfg {
    private String title;
    private String description;
    private Boolean cleanOnExit;

    private Map<Integer, Area> areaByNumber = new TreeMap<>();

    public String getTitle() {
        return title;
    }

    public Collection<Area> getAreas() {
        return Collections.unmodifiableCollection(areaByNumber.values());
    }

    public Area getAreaByNumber(int areaNumber) {
        Area area = areaByNumber.get(areaNumber);
        if (area == null) {
            throw new IllegalArgumentException("Can't find area by number " + areaNumber);
        }
        return area;
    }

    public Area getAreaByLocal(String local) {
        return areaByNumber.values().stream().filter(area -> area.local.equals(local)).findFirst().orElse(null);
    }

    public Area append() {
        int maxAreaNumber = getMaxAreaNumber();

        int newAreaNumber = maxAreaNumber + 1;
        Area area = new Area();
        areaByNumber.put(newAreaNumber, area);
        return area;
    }

    public void remove(Area area) {
        Integer areaNumber = areaByNumber.keySet().stream().filter(key -> areaByNumber.get(key).equals(area)).findFirst().orElse(null);
        if (areaNumber == null) {
            throw new IllegalArgumentException("Can't find the area for removal in the configuration");
        }
        areaByNumber.remove(areaNumber);

        int maxNumber = getMaxAreaNumber();
        int nextNumber = areaNumber;
        for (int currNumber = areaNumber + 1; currNumber <= maxNumber; currNumber++) {
            Area currArea = areaByNumber.get(currNumber);
            if (currArea == null) {
                continue;
            }

            areaByNumber.remove(currNumber);
            areaByNumber.put(nextNumber, currArea);

            nextNumber++;
        }
    }

    private int getMaxAreaNumber() {
        return areaByNumber.keySet().stream().max(Integer::compare).orElse(0);
    }

    public static SceneryCfg load(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String content = new String(bytes);
        String[] lines = content.split("\r\n");

        final int Beginning = 0;
        final int InsideGeneralSection = 1;
        final int AreaHeader = 2;
        final int InsideAreaSection = 3;

        int stage = Beginning;

        SceneryCfg result = new SceneryCfg();
        Area area = null;

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            switch (stage) {
                case Beginning:
                    if (line.equals("[General]")) {
                        stage = InsideGeneralSection;
                    } else {
                        throw new IllegalStateException("Found '" + line + "' when expected '[General]'");
                    }
                    break;

                case InsideGeneralSection:
                    if (isParam(line, "Title")) {
                        result.title = getParamString(line);
                    } else if (isParam(line, "Description")) {
                        result.description = getParamString(line);
                    } else if (isParam(line, "Clean_on_Exit")) {
                        result.cleanOnExit = getParamBoolean(line);
                    } else if (line.equals("")) {
                        // noop
                    } else if (line.startsWith("[Area.")) {
                        stage = AreaHeader;
                        lineIndex--;
                    } else {
                        throw new IllegalStateException("Unknown parameter in line '" + line + "'");
                    }
                    break;

                case AreaHeader:
                    String numberStr = line.substring("[Area.".length(), line.length() - 1);
                    int areaNumber = Integer.parseInt(numberStr);
                    area = result.new Area();
                    result.areaByNumber.put(areaNumber, area);
                    stage = InsideAreaSection;
                    break;

                case InsideAreaSection:
                    if (isParam(line, "Title")) {
                        area.title = getParamString(line);
                    } else if (isParam(line, "Local")) {
                        area.local = getParamString(line);
                    } else if (isParam(line, "Remote")) {
                        area.remote = getParamString(line);
                    } else if (isParam(line, "Active")) {
                        area.active = getParamBoolean(line);
                    } else if (isParam(line, "Required")) {
                        area.required = getParamBoolean(line);
                    } else if (isParam(line, "Layer")) {
                        area.layer = getParamInt(line);
                    } else if (isParam(line, "Texture_ID")) {
                        area.textureId = getParamString(line);
                    } else if (line.equals("")) {
                        // noop
                    } else if (line.startsWith("[Area.")) {
                        area = null;

                        stage = AreaHeader;
                        lineIndex--;
                    } else {
                        throw new IllegalStateException("Unknown parameter in line '" + line + "'");
                    }
                    break;

            }
        }

        return result;
    }

    public void save(String path) throws IOException {
        String RN = "\r\n";

        try (PrintStream out = new PrintStream(path)) {
            out.print("[General]" + RN);
            out.print("Title=" + Misc.mn(title, "") + RN);
            out.print("Description=" + Misc.mn(description, "") + RN);
            out.print("Clean_on_Exit=" + printBoolean(cleanOnExit) + RN);
            out.print(RN);

            for (Integer areaNumber : areaByNumber.keySet()) {
                Area area = areaByNumber.get(areaNumber);
                out.print("[Area." + Str.z(areaNumber, 3) + "]" + RN);
                out.print("Title=" + Misc.mn(area.title, "") + RN);
                if (area.textureId != null) out.print("Texture_ID=" + Misc.mn(area.textureId, "") + RN);
                out.print("Local=" + Misc.mn(area.local, "") + RN);
                if (area.remote != null) out.print("Remote=" + Misc.mn(area.remote, "") + RN);
                out.print("Active=" + printBoolean(area.active) + RN);
                out.print("Required=" + printBoolean(area.required) + RN);
                out.print("Layer=" + areaNumber + RN);
                out.print(RN);
            }
        }
    }

    private static String printBoolean(Boolean bool) {
        if (bool == null) {
            return "";
        }
        return bool ? "TRUE" : "FALSE";
    }

    private static boolean isParam(String line, String paramName) {
        return line.toUpperCase().startsWith(paramName.toUpperCase() + "=");
    }

    private static String getParamString(String line) {
        int index = line.indexOf('=');
        return line.substring(index + 1).trim();
    }

    private static int getParamInt(String line) {
        String value = getParamString(line);
        return Integer.parseInt(value);
    }

    private static Boolean getParamBoolean(String line) {
        String value = getParamString(line);
        if (value.equals("TRUE")) {
            return true;
        } else if (value.equals("FALSE")) {
            return false;
        } else {
            return null;
        }
    }

    public class Area {
        private String title;
        private String local;
        private String remote;
        private String textureId;
        private int layer;
        private Boolean active;
        private Boolean required;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLocal() {
            return local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getRemote() {
            return remote;
        }

        public void setRemote(String remote) {
            this.remote = remote;
        }

        public String getTextureId() {
            return textureId;
        }

        public void setTextureId(String textureId) {
            this.textureId = textureId;
        }

        public int getLayer() {
            return layer;
        }

        public Boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Boolean isRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }
}
