package nextstep.jwp.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHeader {

    public static final String DELIMITER = " ";

    private final String header;
    private final RequestURI requestURI;
    private HttpStatus httpStatus;

    private RequestHeader(String header) {
        this.header = header;
        this.requestURI = new RequestURI(header);
        this.httpStatus = HttpStatus.OK;
    }

    public static RequestHeader from(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder header = new StringBuilder();

        while (bufferedReader.ready()) {
            header.append(bufferedReader.readLine())
                .append("\r\n");
        }

        return new RequestHeader(header.toString());
    }

    public String url() {
        return requestURI.getUrl();
    }

    public URL resource() {
        if (HttpStatus.UNAUTHORIZED == httpStatus) {
            return unauthorized();
        }
        return requestURI.getResource();
    }

    public URL unauthorized() {
        return requestURI.unauthorized();
    }

    public HttpMethod httpMethod() {
        return HttpMethod.findRequest(header);
    }

    public String uri() {
        return requestURI.uri();
    }

    public Map<String, String> queryParam() {
        return requestURI.queryParam();
    }

    public void changeHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public int httpStatusValue() {
        return httpStatus.value();
    }

    public String httpStatusReasonPhrase() {
        return httpStatus().getReasonPhrase();
    }

    public String getHeader() {
        return header;
    }
}
