package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {

    public static final String PREFIX = "/";
    public static final String STATIC = "static";

    private FileUtils() {
    }

    public static String getAbsolutePath(String path) {
        URL resource = FileUtils.class.getClassLoader().getResource(STATIC + path);
        return Objects.requireNonNull(resource).getFile();
    }

    public static String readFileOfUrl(String url) throws IOException {
        if (!url.startsWith(PREFIX)) {
            url = PREFIX + url;
        }

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
