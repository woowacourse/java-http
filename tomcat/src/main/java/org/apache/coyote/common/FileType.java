package org.apache.coyote.common;

import java.util.HashMap;
import java.util.Map;

public enum FileType {
    NONE("", ""),
    TEXT(".text", "text/plain;charset=utf-8"),
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/x-javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml");

    private static final Map<String, FileType> map = new HashMap<>();

    static {
        for (FileType fileType : FileType.values()) {
            map.put(fileType.extension, fileType);
        }
    }

    private final String extension;
    private final String contentType;

    FileType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static FileType get(final String path) {
        if(!path.contains(".")){
            return NONE;
        }
        final String extension = path.substring(path.indexOf("."));
        if(map.containsKey(extension)){
            return map.get(extension);
        }
        return TEXT;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean hasExtension() {
        return this != NONE;
    }

    @Override
    public String toString() {
        return this.extension;
    }
}
