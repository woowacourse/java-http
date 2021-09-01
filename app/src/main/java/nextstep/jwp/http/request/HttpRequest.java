package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static nextstep.jwp.http.HttpCookie.JSESSIONID;
import static nextstep.jwp.http.HttpUtil.parseQuery;

public class HttpRequest {
    private final Map<String, String> header;
    private final Map<String, String> query;
    private final RequestLine requestLine;

    private HttpRequest(RequestLine requestLine, Map<String, String> header, Map<String, String> query) {
        this.header = header;
        this.query = query;
        this.requestLine = requestLine;
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = br.readLine();
        Map<String, String> header = parseHeader(br);
        Map<String, String> query = new HashMap<>();

        if (header.containsKey(CONTENT_LENGTH)) {
            query.putAll(parseQuery(parseBody(br, Integer.parseInt(header.get(CONTENT_LENGTH)))));
        }

        return new HttpRequest(RequestLine.of(requestLine), header, query);
    }

    private static Map<String, String> parseHeader(BufferedReader request) throws IOException {
        HashMap<String, String> header = new HashMap<>();
        String line = null;
        while (true) {
            line = request.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] keyAndValue = line.split(":");
            header.put(keyAndValue[0], keyAndValue[1].trim());
        }
        return header;
    }

    private static String parseBody(BufferedReader request, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        request.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean hasHeaderValue(String headerValue) {
        return header.containsKey(headerValue);
    }

    public String getHeaderValue(String headerValue) {
        return header.get(headerValue);
    }

    public HttpCookie getCookies() {
        return HttpCookie.of(header.get("Cookie"));
    }

    public boolean checkMethod(String method) {
        return requestLine.checkMethod(method);
    }

    public String getQueryValue(String queryValue) {
        return query.get(queryValue);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public HttpSession getSession() {
        return HttpSessions.getSession(getCookies().getCookie(JSESSIONID));
    }
}
