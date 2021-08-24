package nextstep.jwp.http.request.request_line;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpPath {
    private final String path;
    private final Map<String, String> parameters;

    public HttpPath(String uri) {
        this.path = extractPath(uri);
        this.parameters = extractParams(uri);
    }

    private String extractPath(String uri) {
        if(!uri.contains("?")) {
            return uri;
        }

        return uri.substring(0, uri.indexOf("?"));
    }

    private Map<String, String> extractParams(String uri) {
        if(!uri.contains("?")) {
            return new HashMap<>();
        }

        String rawParams = uri.substring(uri.indexOf("?") + 1);
        String[] splitParams = rawParams.split("&");

        return Arrays.stream(splitParams)
            .filter(param -> param.contains("="))
            .map(param -> param.split("="))
            .collect(toMap(param -> param[0], param -> param[1]));
    }

    public File toFile() {
        String path = rewritePath();
        return new File(path);
    }

    private String rewritePath() {
        String path = this.path;

        if(path.startsWith("/")) {
            path = path.substring(1);
        }

        return ClassLoader.getSystemResource(path).getPath();
    }

    public Optional<String> getParam(String key) {
        return Optional.ofNullable(parameters.get(key));
    }

    public String getPath() {
        return path;
    }
}
