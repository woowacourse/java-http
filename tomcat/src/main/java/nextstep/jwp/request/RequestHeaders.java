package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {
    private Map<String, String> headers;

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while(!"".equals(line)) {
            final String[] headerParams = line.split(":");
            headers.put(headerParams[0], headerParams[1].trim());
            line = bufferedReader.readLine();
        }
        return new RequestHeaders(headers);
    }
}
