package nextstep.jwp.request;

import java.util.HashMap;
import java.util.Map;

public class HttpMethod {

    private static final Map<String, HttpMethod> supportingHttpMethods = new HashMap<>();

    static {
        supportingHttpMethods.put("GET", new HttpMethod("GET"));
        supportingHttpMethods.put("POST", new HttpMethod("POST"));
    }

    private final String httpMethod;

    private HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpMethod of(String httpMethod) {
        if (supportingHttpMethods.containsKey(httpMethod)) {
            return supportingHttpMethods.get(httpMethod);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }
}
