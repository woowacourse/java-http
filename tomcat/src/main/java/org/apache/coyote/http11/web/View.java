package org.apache.coyote.http11.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class View {

    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String RESOURCE_DIRECTORY = "static/";

    private final String viewName;

    public View(String viewName) {
        this.viewName = viewName;
    }

    public String renderView() throws IOException {
        final URL resource = findStaticResource();
        if (resource == null) {
            throw new IllegalArgumentException("해당하는 뷰가 존재하지 않습니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL findStaticResource() {
        return getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + viewName);
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }
}
