package nextstep.jwp.http.common;

import nextstep.jwp.http.exception.NotFoundException;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class PathUtils {

    private static final String PREFIX = "static";

    private PathUtils() {
    }

    public static File toFile(String path) {
        path = rewritePath(path);
        return new File(path);
    }

    private static String rewritePath(String path) {
        path = setPrefix(path);

        URL systemResource = ClassLoader.getSystemResource(path);
        if(Objects.isNull(systemResource)) {
            throw new NotFoundException();
        }

        return ClassLoader.getSystemResource(path).getPath();
    }

    private static String setPrefix(String path) {
        if(path.startsWith("/")) {
            return PREFIX + path;
        }
        return PREFIX + "/" + path;
    }
}
