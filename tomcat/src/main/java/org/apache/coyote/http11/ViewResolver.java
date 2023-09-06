package org.apache.coyote.http11;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class ViewResolver {

    private ViewResolver() {
    }

    public static File findViewFile(String path) {
        String resourcePath = findResourcePath(path);
        URL resource = ViewResolver.class.getClassLoader()
                .getResource(String.format("static/%s", resourcePath));

        return new File(Objects.requireNonNull(resource).getFile());
    }

    private static String findResourcePath(String path) {
        if (path.contains(".")) {
            return path;
        }
        return path + ".html";
    }


}
