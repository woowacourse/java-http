package org.apache.coyote;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private static final String CRLF = "\r\n";

    private final String httpMethod;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private String body;
    private final Map<String, String> cookies;

    public HttpRequest(String request) {
        String[] lines = request.split(CRLF);
        String[] requestLine = lines[0].split(" ");
        String pathWithQuery = requestLine[1];
        this.httpMethod = requestLine[0];
        this.path = pathWithQuery.split("\\?")[0];
        this.version = requestLine[2];
        this.headers = parseHeaders(lines);
        this.body = null;
        this.queryParameters = parseQueryParameters(pathWithQuery);
        this.cookies = parseCookies(headers.getOrDefault("Cookie", null));
    }

    public Optional<HttpSession> getSession(SessionManager manager) {
        String jSessionId = cookies.get("JSESSIONID");
        if (jSessionId == null) {
            return Optional.empty();
        }
        HttpSession session = manager.findSession(jSessionId);
        return Optional.ofNullable(session);
    }

    private Map<String, String> parseCookies(String cookie) {
        if (cookie == null) {
            return new HashMap<>();
        }

        Map<String, String> result = new HashMap<>();
        String[] cookies = cookie.split("; ");
        for (String c : cookies) {
            String[] keyValue = c.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private Map<String, String> parseHeaders(String[] lines) {
        Map<String, String> result = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            String[] header = lines[i].split(": ");
            result.put(header[0], header[1]);
        }
        return result;
    }

    private Map<String, String> parseQueryParameters(String path) {
        Map<String, String> result = new HashMap<>();
        String[] pathAndQuery = path.split("\\?");
        if (pathAndQuery.length == 2) {
            String[] params = pathAndQuery[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", -1);
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }

    public String getPath() {
        return path;
    }

    public String getAccept() {
        return headers.getOrDefault("Accept", "*/*");
    }

    public String getMethod() {
        return httpMethod;
    }

    public Map<String, String> parseFormBody() {
        Map<String, String> result = new HashMap<>();
        if (body == null) {
            return result;
        }
        String[] params = body.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    public Optional<String> getFormValue(String key) {
        Map<String, String> formBody = parseFormBody();
        if (!formBody.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(formBody.get(key));
    }

    public Optional<String> getCookie(String key) {
        if (cookies == null || !cookies.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(cookies.get(key));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "body='" + body + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", queryParameters=" + queryParameters +
                ", cookies=" + cookies +
                '}';
    }
}
