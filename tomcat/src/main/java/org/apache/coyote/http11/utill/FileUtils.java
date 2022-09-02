package org.apache.coyote.http11.utill;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtils {

    private static final String BASE_RESOURCE_URL = "static";
    private static final String PERIOD = ".";

    private FileUtils() {
    }

    public static String readFile(final String fileName) throws IOException {
        Objects.requireNonNull(fileName);
        URL resourceUrl = FileUtils.class.getClassLoader().getResource(BASE_RESOURCE_URL + "/" + fileName);

        if (Objects.isNull(resourceUrl)) {
            throw new NotFoundFileException();
        }

        File file = new File(resourceUrl.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static String getFileExtension(final String fileName) {
        Objects.requireNonNull(fileName);

        if (!fileName.contains(PERIOD)) {
            throw new InvalidFileNameException();
        }

        int index = fileName.indexOf(PERIOD);
        return fileName.substring(index);
    }
}
