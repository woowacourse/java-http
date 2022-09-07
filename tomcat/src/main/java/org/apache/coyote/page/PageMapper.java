package org.apache.coyote.page;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private final static String STATIC = "static" + File.separator;

    public static Path getFilePath(final String url) {
        String fileName = FileName.findFileName(url).getFileName();
        return getPath(fileName);
    }

    public static Path getPath(String url) {
        return Paths.get(Objects.requireNonNull(
                PageMapper.class
                .getClassLoader()
                .getResource(STATIC + url))
                .getPath());
    }
}
