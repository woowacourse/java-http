package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {
    private final String requestBody;

    public RequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody createFromBufferedReader(BufferedReader bufferedReader, Integer contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new RequestBody(requestBody);
    }

    public String getRequestBody() {
        return requestBody;
    }
}
