package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {
    private final String body;

    public RequestBody(BufferedReader reader, int contentLength) throws IOException {
        this.body = parseBody(reader, contentLength);
    }

    private String parseBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getBody() {
        return body;
    }
}
