package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> headers;

    public RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }


    public static RequestHeader createFromBufferedReader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            String readLine = bufferedReader.readLine();
            String[] splitHeader = readLine.split(": ");
            if (splitHeader[0].equals("")) {
                break;
            }
            headers.put(splitHeader[0], splitHeader[1]);
        }

        return new RequestHeader(headers);

    }

    public boolean hasHeader(String header) {
        return this.headers.containsKey(header);
    }

    public Integer getBodySize() {
        String contentLength = this.headers.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength.strip());
    }
}
