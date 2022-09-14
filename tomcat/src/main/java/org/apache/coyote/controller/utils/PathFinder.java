package org.apache.coyote.controller.utils;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class PathFinder {

    private PathFinder() {
    }

    public static Path findByFileName(final String fileName) {
        final URL resource = PathFinder.class.getClassLoader().getResource("static" + fileName);
        return new File(resource.getPath()).toPath();
    }
}
