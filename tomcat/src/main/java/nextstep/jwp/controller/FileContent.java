package nextstep.jwp.controller;

import java.util.Arrays;

public enum FileContent {

    INDEX("/index", "/index.html"),
    LOGIN("/login", "/login.html"),
    REGISTER("/register", "/register.html"),
    ERROR_401("/401", "/401.html"),
    ERROR_404("/404", "/404.html"),
    ERROR_500("/500", "/500.html");

    public static final String INDEX_URI = "/index";
    public static final String UNAUTHORIZED_URI = "/401";
    public static final String NOT_FOUND_URI = "/404";
    public static final String INTERNAL_SERVER_ERROR_URI = "/500";
    public static final String STATIC = "static";
    public static final String HTML = ".html";

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
}
