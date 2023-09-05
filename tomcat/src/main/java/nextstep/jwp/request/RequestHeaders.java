package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, String> headers;
    private final Map<String, String> cookies;

    private RequestHeaders(final Map<String, String> headers, final Map<String, String> cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static RequestHeaders of(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            final String[] headerParams = line.split(":");
            headers.put(headerParams[0], headerParams[1].trim());
            line = bufferedReader.readLine();
        }
        final Map<String, String> cookies = storeCookies(headers.get("Cookies"));
        return new RequestHeaders(headers, cookies);
    }

    private static Map<String, String> storeCookies(final String cookies) {
        return Arrays.stream(cookies.split(";"))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0].trim(), cookie -> cookie[1].trim()));
    }

    public String getHeaderValue(final String header) {
        return headers.get(header);
    }

    public boolean hasJSessionCookie() {
        return cookies.containsKey("JSESSIONID");
    }
}
