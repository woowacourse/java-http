package org.apache.coyote;

import static org.apache.coyote.FileName.NOT_FOUND;
import static org.apache.coyote.FileName.findFileName;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private static String STATIC = "static/";

    public static boolean isCustomFileRequest(final String url){
        final FileName foundFileName = findFileName(url);
        return !foundFileName.getFileName().equals("") && !foundFileName.equals(NOT_FOUND);
    }

    public Path getFilePath(String fileName) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + fileName))
                .toURI());
    }

}
