package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.HttpCookie;

import static nextstep.jwp.RequestHandler.LOG;

public class RequestHeaders {
    private final Map<String, String> headers = new HashMap<>();
    private HttpCookie httpCookie;

    public RequestHeaders(BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        while (!"".equals(header)) {
            String[] split = header.split(": ", 2);
            headers.put(split[0], split[1].trim());
            LOG.info("header : {}", header);
            header = bufferedReader.readLine();
        }

        if (headers.containsKey("Cookie")) {
            httpCookie = new HttpCookie(headers.get("Cookie"));
        }
    }

    public String get(String key) {
        return headers.get(key);
    }

    public boolean hasSessionId() {
        if (httpCookie != null) {
            return httpCookie.hasSessionId();
        }
        return false;
    }

    public String getSessionId() {
        return httpCookie.get("JSESSIONID");
    }
}
