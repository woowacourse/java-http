package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

    private FileUtils() {
    }

    public static String readFileOfUrl(String url) throws IOException {
        URL resource = FileUtils.class.getClassLoader().getResource("static" + url);
        String file = Objects.requireNonNull(resource).getFile();
        Path path = new File(file).toPath();
        return new String(Files.readAllBytes(path));
    }

    public static boolean existFile(String url) {
        URL resource = FileUtils.class.getClassLoader().getResource("static" + url);
        return resource != null;
    }
}
