package jakarta.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(value))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
