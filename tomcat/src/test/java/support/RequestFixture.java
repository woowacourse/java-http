package support;

import org.apache.coyote.http11.request.HttpMethod;

public class RequestFixture {

    private RequestFixture() {
    }

    public static String create(final HttpMethod method, final String url, final String body) {
        return String.format("%s %s HTTP/1.1 \r\nHost: localhost:8080 \r\nConnection: keep-alive \r\nContent-Length: %d \r\n\r\n%s",
                method.getMethodName(), url, body.getBytes().length, body);
    }
}
