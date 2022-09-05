package support;

import org.apache.coyote.HttpStatus;

public class ResponseFixture {

    private ResponseFixture() {
    }

    public static String create(final HttpStatus status, final String mimeType, final String body) {
        final String responseFormat = String.join("\r\n",
                "HTTP/1.1 %d %s ",
                "Content-Type: %s;charset=utf-8 ",
                "Content-Length: %d ",
                "",
                "%s");
        return String.format(responseFormat,
                status.getStatusNumber(), status.getStatusName(),
                mimeType,
                body.getBytes().length,
                body);
    }
}
