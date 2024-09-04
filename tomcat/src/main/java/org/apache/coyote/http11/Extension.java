package org.apache.coyote.http11;

public enum Extension {

    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    PNG("png"),
    JPG("jpg"),
    ;

    private final String value;

    Extension(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isFileExtension(String path) {
        for (Extension fileExtension : values()) {
            if (path.endsWith(fileExtension.getValue())) {
                return true;
            }
        }
        return false;
    }
}
