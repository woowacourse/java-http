package org.apache.coyote.http11.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileFinder {

    private static final String STATIC_FOLDER_NAME = "static";

    private final FileDetails fileDetails;

    public FileFinder(FileDetails fileDetails) {
        this.fileDetails = fileDetails;
    }

    public String resolve() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(STATIC_FOLDER_NAME + fileDetails.getFilePath());
            if (Objects.isNull(resource)) {
                throw new NoResourceFoundException(fileDetails.getFilePath() + "파일이 존재하지 않습니다.");
            }

            File file = new File(resource.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new NoResourceFoundException(fileDetails.getFilePath() + "파일이 존재하지 않습니다.");
        }
    }
}
