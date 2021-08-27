package nextstep.jwp.util;

public class FilePathFinder {

    private static final String FILE_FORMAT = ".html";
    private static final String PATH_PREFIX = "static";

    public static String findPath(String uriPath) {
        if (uriPath.endsWith(FILE_FORMAT)) {
            return String.format(PATH_PREFIX + "%s", uriPath);
        }
        return String.format(PATH_PREFIX + "%s" + FILE_FORMAT, uriPath);
    }
}
