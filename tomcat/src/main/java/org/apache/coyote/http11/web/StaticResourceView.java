package org.apache.coyote.http11.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import org.apache.coyote.http11.request.ResourceContentType;

public class StaticResourceView implements View {

    private static final String RESOURCE_DIRECTORY = "static/";
    private static final String NOT_FOUND_RESOURCE = "static/404.html";

    private final String viewName;
    private final String contentType;

    public StaticResourceView(final String viewName) {
        this.viewName = viewName;
        this.contentType = ResourceContentType.from(viewName).getContentType();
    }

    public String renderView() throws IOException {
        final URL resource = findStaticResource();
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL findStaticResource() {
        return Optional.ofNullable(getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + viewName))
                .orElse(getClass().getClassLoader().getResource(NOT_FOUND_RESOURCE));
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
