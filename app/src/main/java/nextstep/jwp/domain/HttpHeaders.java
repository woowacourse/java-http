package nextstep.jwp.domain;

import java.util.HashMap;

public class HttpHeaders {

    private final HashMap<String, String> headers;

    public HttpHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(HashMap<String, String> headers) {
        return new HttpHeaders(headers);
    }


}
