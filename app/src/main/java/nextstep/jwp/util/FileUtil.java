package nextstep.jwp.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static final List<String> STATIC_FILE_FORMATS = Arrays.asList(".html", ".js", ".css");
    private static final String HTML_FILE_FORMAT = ".html";
    private static final String PATH_PREFIX = "static";

    public static String readStaticFileByUriPath(String uriPath) {
        return readFile(findStaticFilePath(uriPath));
    }

    private static String readFile(String uriPath) {
        try {
            URL resource = FileUtil.class.getClassLoader().getResource(uriPath);
            return new String(Files.readAllBytes(Paths.get(resource.getFile())));
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private static String findStaticFilePath(String uriPath) {
        if (isFileFormat(uriPath)) {
            return String.format(PATH_PREFIX + "%s", uriPath);
        }
        return String.format(PATH_PREFIX + "%s" + HTML_FILE_FORMAT, uriPath);
    }

    private static boolean isFileFormat(String uriPath) {
        return STATIC_FILE_FORMATS.stream()
            .anyMatch(uriPath::endsWith);
    }
}
