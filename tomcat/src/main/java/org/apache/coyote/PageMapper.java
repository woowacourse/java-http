package org.apache.coyote;

import static org.apache.coyote.FileName.findFileName;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private static String STATIC = "static/";

    private String fileName;

    public PageMapper(String url) {
        this.fileName = findFileName(url).getFilePath();
    }

    public static boolean isFileRequest(final String url){
        return findFileName(url).getFilePath() != null;
    }

    public Path getFilePath() throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + fileName))
                .toURI());
    }

}
