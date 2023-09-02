package nextstep.jwp.protocol.request_line;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {

    private static String PATH_PATTERN = "^/([\\w/.-]*)(\\?[^\\s]*)?$";

    private final String path;

    private Path(String path) {
        this.path = path;
    }

    public static Path from(String path) {
        validatePath(path);
        return new Path(path);
    }

    private static void validatePath(String path) {
        Pattern compiledPattern = Pattern.compile(PATH_PATTERN);
        Matcher matcher = compiledPattern.matcher(path);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 URI입니다. 올바른 URI인지 다시 확인해주세요.");
        }
    }

    public String path() {
        return this.path;
    }

}
