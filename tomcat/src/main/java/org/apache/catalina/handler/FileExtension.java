package org.apache.catalina.handler;

public enum FileExtension {
    HTML(".html", "text/html;charset=utf-8"),
    HTM(".htm", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    PNG(".png", "image/png"),
    ICO(".ico", "image/x-icon");

    private static final FileExtension DEFAULT = HTML;

    private final String extension;
    private final String mimeType;

    FileExtension(final String extension, final String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static FileExtension fromExtension(final String extension) {
        if (extension == null) {
            return null;
        }

        for (FileExtension fileExt : values()) {
            if (fileExt.extension.equalsIgnoreCase(extension)) {
                return fileExt;
            }
        }
        return null;
    }

    public static boolean isSupported(final String extension) {
        return fromExtension(extension) != null;
    }
}

