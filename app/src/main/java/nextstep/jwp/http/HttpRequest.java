package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private final BufferedReader bufferedReader;

    public HttpRequest(InputStream inputStream) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        requestLine = new RequestLine(bufferedReader);
        parseHeaders();
        parseParams();
    }

    private void parseHeaders() throws IOException {
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (Objects.isNull(line) || line.isEmpty()) {
                return;
            }
            String[] header = line.split(": ");
            headers.put(header[0].trim(), header[1].trim());
        }
    }

    private void parseParams() throws IOException {
        if (headers.containsKey("Content-Length")) {
            String[] queries = extractRequestBody().split("&");
            for (String query : queries) {
                int equalIndex = query.indexOf("=");
                String key = query.substring(0, equalIndex);
                String value = query.substring(equalIndex + 1);
                params.put(key, value);
            }
        }
    }

    private String extractRequestBody() throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpSession getSession() {
        String cookie = headers.get("Cookie");
        if (cookie != null) {
            HttpCookies httpCookies = new HttpCookies(cookie);
            String sessionId = httpCookies.getCookies().get("JSESSIONID");
            if (HttpSessions.find(sessionId) == null) {
                String id = UUID.randomUUID().toString();
                HttpSession httpSession = new HttpSession(id);
                HttpSessions.add(id, httpSession);
                return httpSession;
            }
            return HttpSessions.find(sessionId);
        }
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);
        HttpSessions.add(uuid, httpSession);
        return httpSession;
    }
}
