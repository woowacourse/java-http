package nextstep.jwp.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * /login?something1=123&something2=123
 */

public class RequestUriPath {
    private final String path;
    private final Map<String, String> params;

    public RequestUriPath(String path, Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static RequestUriPath of(String uriPath) {
        if (hasParams(uriPath)) {
            int index = uriPath.indexOf("?");
            String path = uriPath.substring(0, index);
            String queryString = uriPath.substring(index + 1);

            return new RequestUriPath(path, params(queryString));
        }

        return new RequestUriPath(uriPath, Collections.emptyMap());
    }

    private static boolean hasParams(String path){
        return path.split("/?").length > 1;
    }

    private static Map<String, String> params(String paramsLine) {
        Map<String, String> params = new HashMap<>();
        for (String pair : paramsLine.split("&")) {
            String[] keyValue = pair.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    public String getPath() {
        return path;
    }
}
