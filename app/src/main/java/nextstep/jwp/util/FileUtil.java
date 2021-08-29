package nextstep.jwp.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    private static final String FILE_FORMAT = ".html";
    private static final String PATH_PREFIX = "static";

    public static String readFileByUriPath(String uriPath) {
        try {
            URL resource = FileUtil.class.getClassLoader().getResource(findPath(uriPath));
            return new String(Files.readAllBytes(Paths.get(resource.getFile())));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private static String findPath(String uriPath) {
        if (uriPath.endsWith(FILE_FORMAT)) {
            return String.format(PATH_PREFIX + "%s", uriPath);
        }
        return String.format(PATH_PREFIX + "%s" + FILE_FORMAT, uriPath);
    }
}
