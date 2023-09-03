package nextstep.jwp.protocol.request_line;

import java.util.Map;
import nextstep.jwp.protocol.request_line.vo.DefaultPath;
import nextstep.jwp.protocol.request_line.vo.QueryString;

public class Path {

    private static String DEFAULT_URL_QUERY_STRING_SEPARATOR = "\\?";

    private final DefaultPath defaultPath;
    private final QueryString queryString;

    private Path(DefaultPath defaultPath, QueryString queryString) {
        this.defaultPath = defaultPath;
        this.queryString = queryString;
    }

    public static Path from(String path) {
        String[] paths = path.split(DEFAULT_URL_QUERY_STRING_SEPARATOR);
        if (paths.length == 1) {
            return new Path(DefaultPath.from(paths[0]), QueryString.from(null));
        }
        return new Path(DefaultPath.from(paths[0]), QueryString.from(paths[1]));
    }

    public String defaultPath() {
        return this.defaultPath.value();
    }

    public Map<String, String> queryString() {
        return queryString.value();
    }

}
