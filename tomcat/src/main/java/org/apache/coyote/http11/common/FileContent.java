package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum FileContent {

    INDEX("/index", "/index.html"),
    LOGIN("/login", "/login.html"),
    REGISTER("/register", "/register.html"),
    ERROR_401("/401", "/401.html"),
    ERROR_404("/404", "/404.html"),
    ERROR_500("/500", "/500.html");

    private final String fileName;
    private final String fileNameWithExtension;

    FileContent(final String fileName, final String fileNameWithExtension) {
        this.fileName = fileName;
        this.fileNameWithExtension = fileNameWithExtension;
    }

    public static String findPage(final String name) {
        return Arrays.stream(FileContent.values())
                .filter(value -> value.fileName.equals(name) || value.fileNameWithExtension.equals(name))
                .map(value -> value.fileName)
                .findFirst()
                .orElse(ERROR_404.getFileName());
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileNameWithExtension() {
        return fileNameWithExtension;
    }
}
