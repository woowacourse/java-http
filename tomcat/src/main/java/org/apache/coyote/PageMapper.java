package org.apache.coyote;

import static org.apache.coyote.FileName.NOT_FOUND;
import static org.apache.coyote.FileName.findFileName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private static String STATIC = "static/";

    public static boolean isCustomFileRequest(final String url){
        final FileName foundFileName = findFileName(url);
        return !foundFileName.getFileName().equals("") && !foundFileName.equals(NOT_FOUND);
    }

    public String makeResponseBody(String url) {
        if (isCustomFileRequest(url)) {
            try {
                String fileName = FileName.findFileName(url).getFileName();
                return readFile(getFilePath(fileName));
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return readFile(getFilePath(url));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return "Hello world!";
    }

    private String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private String getFilePath(String fileName) throws URISyntaxException {
        return Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + fileName))
                .getPath();
    }

}
