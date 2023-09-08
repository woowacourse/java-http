package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileFinder {

    public static File getFile(String uri) {
        String fileUrl = "static" + uri;
        return new File(
            FileFinder.class
                .getClassLoader()
                .getResource(fileUrl)
                .getFile()
        );
    }

    public static String getFileContent(String uri) {
        File file = getFile(uri);
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
