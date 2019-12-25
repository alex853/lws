package net.simforge.scenery.core.dto;

public class StepDto {

    private String file;
    private String source;
    private String dest;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "StepDto{" +
                "file='" + file + '\'' +
                ", source='" + source + '\'' +
                ", dest='" + dest + '\'' +
                '}';
    }
}
