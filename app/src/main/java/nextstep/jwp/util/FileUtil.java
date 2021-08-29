package nextstep.jwp.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    private static final String HTML_FILE_FORMAT = ".html";
    private static final String PATH_PREFIX = "static";

    public static String readHTMLFileByUriPath(String uriPath) {
        return readFile(findHTMLFilePath(uriPath));
    }

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
        return String.format(PATH_PREFIX + "%s", uriPath);
    }

    private static String findHTMLFilePath(String uriPath) {
        if (uriPath.endsWith(HTML_FILE_FORMAT)) {
            return String.format(PATH_PREFIX + "%s", uriPath);
        }
        return String.format(PATH_PREFIX + "%s" + HTML_FILE_FORMAT, uriPath);
    }
}
