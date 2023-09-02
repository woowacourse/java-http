package nextstep.jwp.protocol.request_line;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Path {

    private static String PATH_PATTERN = "^/([\\w/.-]*)(\\?[^\\s]*)?$";
    private static String DEFAULT_URL_QUERY_STRING_SEPARATOR = "\\?";
    private static String QUERY_STRING_SEPARATOR = "&";
    private static String QUERY_STRING_KEY_VALUE_SEPARATOR = "=";

    private final String defaultPath;
    private final Map<String, String> queryString;

    private Path(String originPath, Map<String, String> queryString) {
        this.defaultPath = originPath;
        this.queryString = queryString;
    }

    public static Path from(String path) {
        validatePath(path);
        String[] paths = path.split(DEFAULT_URL_QUERY_STRING_SEPARATOR);
        if (paths.length == 1) {
            return new Path(paths[0], null);
        }
        return new Path(paths[0], getQueryStringPairs(paths[1]));
    }

    private static void validatePath(String path) {
        Pattern compiledPattern = Pattern.compile(PATH_PATTERN);
        Matcher matcher = compiledPattern.matcher(path);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 URI입니다. 올바른 URI인지 다시 확인해주세요.");
        }
    }

    private static Map<String, String> getQueryStringPairs(String queryString) {
        List<String> queryStringKeyAndValue = Arrays.asList(queryString.split(QUERY_STRING_SEPARATOR));

        return queryStringKeyAndValue.stream()
                .map(keyValuePair -> keyValuePair.split(QUERY_STRING_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    public String defaultPath() {
        return this.defaultPath;
    }

    public Map<String, String> queryString() {
        return queryString;
    }

}
