package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

public class HttpRequest {

    private final Set<String> ALLOWED_METHOD = Set.of("GET", "POST");
    private final String ALLOWED_VERSION = "HTTP/1.1";
    private static final String ROOT_PATH = "/";
    private static final String STATIC_TARGET_PATH = "static";
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

    private final String method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final String version;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> httpRequestBody;
    private final HttpCookie httpCookie;

    private HttpRequest(String method, String path, Map<String, String> queryParameters, String version,
                        Map<String, String> httpRequestHeaders, Map<String, String> httpRequestBody, HttpCookie httpCookie) {
        validate(method, version);
        this.method = method;
        this.path = path;
        this.queryParameters = queryParameters;
        this.version = version;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestBody = httpRequestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        String line = readLine(bufferedInputStream);
        StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
        String method = stringTokenizer.nextToken();
        String requestUri = stringTokenizer.nextToken();
        String version = stringTokenizer.nextToken();
        Map<String, String> httpRequestHeaders = new HashMap<>();

        while (!(line = readLine(bufferedInputStream)).isEmpty()) {
            String[] header = line.split(":", 2);
            httpRequestHeaders.put(header[0].strip(), header[1].strip());
        }
        HttpCookie httpCookie = HttpCookie.from(httpRequestHeaders.get("Cookie"));

        if (method.equals("GET")) {
            return createGetRequest(method, requestUri, version, httpRequestHeaders, httpCookie);
        }
        return createPostRequest(method, requestUri, version, httpRequestHeaders, bufferedInputStream, httpCookie);
    }

    public boolean isRoot() {
        return "GET".equals(method) && ROOT_PATH.equals(path);
    }

    public boolean hasBodyParameters(String ... names) {
        for (String name : names) {
            if (!httpRequestBody.containsKey(name)) {
                return false;
            }
        }
        return true;
    }

    public String getBodyParameter(String key) {
        return httpRequestBody.get(key);
    }

    public String getResourcePath() {
        return STATIC_TARGET_PATH + path;
    }

    public boolean isGetLoginRequest() {
        return "GET".equals(method) && "/login".equals(path);
    }

    public boolean isGetRegisterRequest() {
        return "GET".equals(method) && "/register".equals(path);
    }

    public boolean isPostLoginRequest() {
        return "POST".equals(method) && "/login".equals(path);
    }

    public boolean isPostRegisterRequest() {
        return "POST".equals(method) && "/register".equals(path);
    }

    public HttpSession getSession(boolean isCreate) throws IOException {
        HttpSession session = SESSION_MANAGER.findSession(httpCookie.getJsessionid());
        if (isCreate && session == null) {
            String id = UUID.randomUUID().toString();
            Session newSession = new Session(id);
            SESSION_MANAGER.add(newSession);
            return newSession;
        }
        return session;
    }

    public String getJsessionid() {
        return httpCookie.getJsessionid();
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        int current;
        while ((current = inputStream.read()) != '\r' && current != -1) {
            line.write(current);
        }

        if (current == -1) {
            return convertLineOrNull(line);
        }
        validateLineFeed(inputStream);
        return convertToString(line);
    }

    private static String convertLineOrNull(ByteArrayOutputStream line) {
        if (line.size() == 0) {
            return null;
        }
        return convertToString(line);
    }

    private static void validateLineFeed(InputStream inputStream) throws IOException {
        if (inputStream.read() != '\n') {
            throw new IOException("잘못된 HTTP 줄바꿈입니다.");
        }
    }

    private static String convertToString(ByteArrayOutputStream line) {
        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private static HttpRequest createGetRequest(
            String method, String requestUri, String version,
            Map<String, String> httpRequestHeaders, HttpCookie httpCookie) {
        int index = requestUri.indexOf("?");
        if (index == -1) {
            return new HttpRequest(method, requestUri, Map.of(), version, httpRequestHeaders, Map.of(), httpCookie);
        }
        String path = requestUri.substring(0, index);
        Map<String, String> queryParameters = parseParameters(requestUri.substring(index + 1));
        return new HttpRequest(method, path, queryParameters, version, httpRequestHeaders, Map.of(), httpCookie);
    }

    private static HttpRequest createPostRequest(
            String method, String requestUri, String version, Map<String, String> httpRequestHeaders,
            InputStream inputStream, HttpCookie httpCookie) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        byte[] body = inputStream.readNBytes(contentLength);

        if (body.length != contentLength) {
            throw new IOException("요청 본문의 길이가 Content-Length와 일치하지 않습니다.");
        }

        String requestBody = new String(body, StandardCharsets.UTF_8);
        return new HttpRequest(
                method, requestUri, Map.of(), version, httpRequestHeaders, parseParameters(requestBody), httpCookie);
    }

    private static Map<String, String> parseParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(queryString, "&");

        while (stringTokenizer.hasMoreTokens()) {
            String parameter = stringTokenizer.nextToken();
            int index = parameter.indexOf("=");
            String key = URLDecoder.decode(parameter.substring(0, index), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(parameter.substring(index + 1), StandardCharsets.UTF_8);
            queryParameters.put(key, value);
        }
        return queryParameters;
    }

    private void validate(String method, String version) {
        validateMethod(method);
        validateVersion(version);
    }

    private void validateMethod(String method) {
        if (ALLOWED_METHOD.contains(method)) {
            return;
        }
        throw new IllegalArgumentException("400 지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private void validateVersion(String version) {
        if (ALLOWED_VERSION.equals(version)) {
            return;
        }
        throw new IllegalArgumentException("400 지원하지 않는 HTTP 버전입니다: " + version);
    }
}
