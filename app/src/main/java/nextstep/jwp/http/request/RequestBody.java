package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestBody {
    
    private final String value;
    
    public RequestBody(String value) {
        this.value = value;
    }

    public static RequestBody parse(BufferedReader bufferedReader, HttpHeaders headers)
        throws IOException {
        if (headers.hasRequestBody()) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return new RequestBody(String.valueOf(buffer));
        }

        return new RequestBody(null);
    }
}
