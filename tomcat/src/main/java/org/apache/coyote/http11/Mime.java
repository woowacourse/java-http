package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Mime {

    TXT(FileExtension.TXT, "text/plain"),
    HTML(FileExtension.HTML, "text/html"),
    JAVASCRIPT(FileExtension.JAVASCRIPT, "application/javascript"),
    JSON(FileExtension.JSON, "application/json"),
    CSS(FileExtension.CSS, "text/css"),
    DEFAULT(null, "application/octet-stream"),
    ;

    private static final Map<FileExtension, Mime> BY_EXTENSION;
    private static final Map<String, Mime> BY_EXTENSION_TEXT;

    static {
        final Map<FileExtension, Mime> byExtension = new HashMap<>();
        final Map<String, Mime> byExtensionText = new HashMap<>();
        for (Mime mime : values()) {
            if (mime == DEFAULT) continue;
            byExtension.put(mime.extension, mime);
            for (String extension : mime.extension.getExtensions()) {
                byExtensionText.put(extension, mime);
            }
        }
        BY_EXTENSION = Collections.unmodifiableMap(byExtension);
        BY_EXTENSION_TEXT = Collections.unmodifiableMap(byExtensionText);
    }

    private final FileExtension extension;
    private final String type;

    Mime(FileExtension extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String getMimeTypeValue(final FileExtension fileExtension) {
        return BY_EXTENSION.getOrDefault(fileExtension, DEFAULT).type;
    }

    public static String getMimeTypeValue(final String fileExtension) {
        return BY_EXTENSION_TEXT.getOrDefault(fileExtension, DEFAULT).type;
    }

    public FileExtension getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
