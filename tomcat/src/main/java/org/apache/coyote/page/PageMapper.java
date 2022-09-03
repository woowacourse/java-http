package org.apache.coyote.page;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.page.FileName;

public class PageMapper {

    private final static String STATIC = "static" + File.separator;

    public Path getStaticFilePath(final String url){
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + url))
                .getPath());
    }

    public Path getFilePath(final String url){
        String fileName = FileName.findFileName(url).getFileName();
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + fileName))
                .getPath());
    }
}
