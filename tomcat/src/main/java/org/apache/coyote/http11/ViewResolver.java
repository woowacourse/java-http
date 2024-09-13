package org.apache.coyote.http11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewResolver {

    private static final String PREFIX = "static";

    private static class ViewResolverHolder {
        private static final ViewResolver INSTANCE = new ViewResolver();
    }

    public static ViewResolver getInstance() {
        return ViewResolver.ViewResolverHolder.INSTANCE;
    }

    public String resolveViewName(String viewName) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(PREFIX + viewName);
        if (resource == null) {
            throw new FileNotFoundException("페이지가 없습니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
