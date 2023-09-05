package org.apache.coyote.http11.mvc.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import org.apache.coyote.http11.common.ResourceContentType;

public class StaticResourceView implements View {

    private static final String RESOURCE_DIRECTORY = "static/";

    private final URL viewPath;
    private final String contentType;

    public StaticResourceView(final URL viewPath, final String contentType) {
        this.viewPath = viewPath;
        this.contentType = contentType;
    }

    public static StaticResourceView of(final String viewName) {
        final ClassLoader classLoader = StaticResourceView.class.getClassLoader();
        final URL viewPath = Optional.ofNullable(classLoader.getResource(RESOURCE_DIRECTORY + viewName))
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + viewName));
        final String contentType = ResourceContentType.from(viewName).getContentType();
        return new StaticResourceView(viewPath, contentType);
    }

    @Override
    public String renderView() throws IOException {
        return new String(Files.readAllBytes(new File(viewPath.getFile()).toPath()));
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
