package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewResolver {

    private static final String PREFIX = "static";
    private static final String NOT_FOUND_VIEW = "/404.html";

    public String resolveViewName(String viewName) throws IOException {
        URL resource = findURL(viewName);
        if (resource == null) {
            resource = findURL(NOT_FOUND_VIEW);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL findURL(String viewName) {
        return getClass().getClassLoader().getResource(PREFIX + viewName);
    }

    private ViewResolver() {
    }

    public static ViewResolver getInstance() {
        return ViewResolver.ViewResolverHolder.INSTANCE;
    }

    private static class ViewResolverHolder {
        private static final ViewResolver INSTANCE = new ViewResolver();
    }
}
