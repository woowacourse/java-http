package support;

import org.apache.coyote.http11.response.HttpStatus;

public class ResponseFixture {

    private ResponseFixture() {
    }

    public static String create(final HttpStatus status, final String mimeType, final String body) {
        final String responseFormat = String.join("\r\n",
                "HTTP/1.1 %d %s ",
                "Content-Length: %d ",
                "Content-Type: %s;charset=utf-8 ",
                "",
                "%s");
        return String.format(responseFormat,
                status.getStatusNumber(), status.getStatusName(),
                body.getBytes().length,
                mimeType,
                body);
    }
}
