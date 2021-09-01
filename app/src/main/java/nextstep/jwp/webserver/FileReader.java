package nextstep.jwp.webserver;

import nextstep.jwp.application.exception.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private static final FileReader fileReader = new FileReader();

    private FileReader() {
    }

    public static String readStaticFile(String fileName) {
        return fileReader.readFile(fileName);
    }

    private String readFile(String fileName) {
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);
        if (resource == null) {
            throw new NotFoundException();
        }
        Path path = new File(resource.getPath()).toPath();

        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 read 중 에러 발생");
        }
    }

}
