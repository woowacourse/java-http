package org.apache.coyote.util;

public enum FileExtension {

    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    PNG("png"),
    JPG("jpeg"),
    SVG("svg"),
    ;

    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public static boolean isFileExtension(String path) {
        for (FileExtension ext : values()) {
            if (path.endsWith(ext.getExtension())) {
                return true;
            }
        }
        return false;
    }

    public String getExtension() {
        return extension;
    }

    public static FileExtension from(String path) {
        for (FileExtension ext : values()) {
            if (path.endsWith(ext.getExtension())) {
                return ext;
            }
        }
        throw new IllegalArgumentException();
    }
}
