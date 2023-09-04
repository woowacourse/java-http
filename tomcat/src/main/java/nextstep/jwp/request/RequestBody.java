package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {

    private String content;

    private RequestBody(final String content) {
        this.content = content;
    }

    public static RequestBody of(final BufferedReader bufferedReader, final String contentLength) throws IOException {
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new RequestBody(new String(buffer));
    }

    public String getContent() {
        return content;
    }
}
