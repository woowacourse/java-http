package nextstep.jwp.http;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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
            if (Strings.isNullOrEmpty(line)) {
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

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getResource() {
        return requestLine.getResource();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpSession getSession() {
        if (headers.containsKey("Cookie")) {
            String cookie = headers.get("Cookie");
            HttpCookies httpCookies = new HttpCookies(cookie);
            if (httpCookies.hasKey("JSESSIONID")) {
                String sessionId = httpCookies.getCookie("JSESSIONID");
                return HttpSessions.find(sessionId);
            }
        }
        return null;
    }
}
