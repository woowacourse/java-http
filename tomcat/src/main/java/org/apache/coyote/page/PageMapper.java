package org.apache.coyote.page;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private final static String STATIC = "static" + File.separator;

    public Path getStaticFilePath(final String url){
        return getPath(url);
    }

    public Path getFilePath(final String url){
        String fileName = FileName.findFileName(url).getFileName();
        return getPath(fileName);
    }

    private Path getPath(String url) {
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + url))
                .getPath());
    }
}
