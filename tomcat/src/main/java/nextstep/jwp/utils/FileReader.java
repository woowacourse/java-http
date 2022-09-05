package nextstep.jwp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileReader {

    public static String readFile(String filePath) throws IOException {
        final URL resource = FileReader.class.getClassLoader().getResource("static" + filePath);
        return Files.readString(new File(Objects.requireNonNull(resource).getFile()).toPath());
    }
}
