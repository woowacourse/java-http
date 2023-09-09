package org.apache.coyote.http11.mvc.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import org.apache.coyote.http11.common.ResourceContentType;

public class StaticResourceView implements View {

    private static final String RESOURCE_DIRECTORY = "static/";

    private final String viewName;
    private final String contentType;

    public StaticResourceView(final String viewName, final String contentType) {
        this.viewName = viewName;
        this.contentType = contentType;
    }

    public static StaticResourceView of(final String viewName) {
        return new StaticResourceView(viewName, ResourceContentType.from(viewName).getContentType());
    }

    @Override
    public String renderView() {
        try {
            final ClassLoader classLoader = StaticResourceView.class.getClassLoader();
            final URL viewPath = Optional.ofNullable(classLoader.getResource(RESOURCE_DIRECTORY + viewName))
                    .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + viewName));
            return new String(Files.readAllBytes(new File(viewPath.getFile()).toPath()));
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
