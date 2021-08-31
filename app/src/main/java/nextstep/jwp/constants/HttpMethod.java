package nextstep.jwp.constants;

import java.util.Arrays;
import nextstep.jwp.exception.HttpException;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod findHttpMethod(String methodName) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.method.equalsIgnoreCase(methodName))
                .findFirst()
                .orElseThrow(() -> new HttpException("해당하는 http 메소드가 없습니다."));
    }
}
