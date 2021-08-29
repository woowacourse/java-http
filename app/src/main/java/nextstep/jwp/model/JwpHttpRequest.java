package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;

public class JwpHttpRequest {

    private static final String REQUEST_SEPARATOR = " ";

    private final String method;
    private final String uri;
    private final String httpVersion;

    public JwpHttpRequest(String method, String uri, String httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static JwpHttpRequest of(BufferedReader reader) throws IOException {
        String requestHeader = reader.readLine();
        if (requestHeader == null) {
            throw new IllegalArgumentException("올바르지 않은 요청입니다.");
        }

        String[] requestInfos = requestHeader.split(REQUEST_SEPARATOR);
        String requestMethod = requestInfos[0];
        String requestUri = requestInfos[1];
        String requestHttpVersion = requestInfos[2];

        String line = reader.readLine();
        while (!line.isBlank()) { //todo: headers와 params 요소도 담는게 좋지않을가? (나머지
            line = reader.readLine();
        }

        return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
