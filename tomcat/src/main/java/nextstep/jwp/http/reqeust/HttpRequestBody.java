package nextstep.jwp.http.reqeust;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestBody {

    private static final String EMPTY_REQUEST_BODY = " ";

    private String requestBody;

    public HttpRequestBody(final BufferedReader bufferReader, final String contentLength) throws IOException {
        this.requestBody = requestBody(bufferReader, contentLength);
    }

    private String requestBody(final BufferedReader bufferReader, final String contentLength) throws IOException {
        if (contentLength == null || contentLength.equals("0")) {
            return EMPTY_REQUEST_BODY;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferReader.read(buffer, 0, length);
        return new String(buffer);
    }

    public String getBody() {
        return requestBody;
    }
}
