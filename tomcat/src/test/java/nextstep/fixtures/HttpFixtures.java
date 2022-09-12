package nextstep.fixtures;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpMime;
import org.apache.coyote.HttpStatus;

public class HttpFixtures {

    public static String 요청을_생성한다(final HttpMethod httpMethod, final String uri, final HttpMime contentType) {
        return String.join("\r\n",
                httpMethod.name() + " " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: " + contentType.getValue() + ",*/*;q=0.1",
                "");
    }

    public static String 컨텐트를_포함하여_요청을_생성한다(final HttpMethod httpMethod, final String uri, final HttpMime contentType, final String body) {
        return String.join("\r\n",
                httpMethod.name() + " " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: " + contentType.getValue() + ",*/*;q=0.1",
                "Content-Length: " + body.getBytes().length,
                "",
                body);
    }

    public static String 응답을_생성한다(final HttpStatus httpStatus, final String contentType, final String content) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }
}
