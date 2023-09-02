package nextstep.jwp.protocol.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {

    private static String PATH_PATTERN = "^/([\\w/.-]*)$";

    private final String path;

    public Path(String path) {
        validatePath(path);
        this.path = path;
    }

    private void validatePath(String path) {
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
