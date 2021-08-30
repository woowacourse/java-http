package nextstep.jwp.http.request.requestline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestURI {

    private final String requestURI;
    private final Map<String, String> parameters;

    public RequestURI(String requestURI) {
        this.requestURI = extractURI(requestURI);
        this.parameters = extractParams(requestURI);
    }

    private String extractURI(String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private Map<String, String> extractParams(String uri) {
        if (!uri.contains("?")) {
            return new HashMap<>();
        }
        String params = uri.substring(uri.indexOf("?") + 1);
        String[] splitParams = params.split("&");

        return Arrays.stream(splitParams)
            .map(param -> param.split("="))
            .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    public String getParamValue(String key) {
        return parameters.get(key);
    }

    public String getRequestURI() {
        return requestURI;
    }
}
