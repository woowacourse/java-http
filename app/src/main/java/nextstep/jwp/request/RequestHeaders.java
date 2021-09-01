package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static nextstep.jwp.RequestHandler.LOG;

public class RequestHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public RequestHeaders(BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        while (!"".equals(header)) {
            String[] split = header.split(": ", 2);
            headers.put(split[0], split[1].trim());
            LOG.info("header : {}", header);
            header = bufferedReader.readLine();
        }
    }

    public String get(String key) {
        return headers.get(key);
    }
}
