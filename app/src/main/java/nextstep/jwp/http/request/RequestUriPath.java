package nextstep.jwp.http.request;

import java.util.*;

/**
 * /login?something1=123&something2=123
 */

public class RequestUriPath {
    private final String path;
    private final Map<String, String> params;

    private RequestUriPath(String path, Map<String, String> params) {
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
        return path.indexOf("?") > 0;
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

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestUriPath that = (RequestUriPath) o;
        return Objects.equals(path, that.path) && Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, params);
    }
}
