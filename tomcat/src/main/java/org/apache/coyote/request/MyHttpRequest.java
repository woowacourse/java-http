package org.apache.coyote.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.cookie.HttpCookie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyHttpRequest {

    private static final String RESOURCE_PATH_PREFIX = "static";

    private SessionManager manager = SessionManager.getInstance();
    private Session session;
    private boolean isNewSession;
    private Method method;
    private String uri;
    private String resourcePath;
    private ContentType contentType;
    private String version;
    private HttpCookie cookie;
    private String body;

    public MyHttpRequest(Method method, String uri, String resourcePath, ContentType contentType, String version,
                         HttpCookie cookie, String body) {
        this.method = method;
        this.uri = uri;
        this.resourcePath = RESOURCE_PATH_PREFIX + resolveResourcePath(resourcePath);
        this.contentType = contentType;
        this.version = version;
        this.cookie = cookie;
        this.body = body;
    }

    public static MyHttpRequest of(String rawRequest) {
        String requestLine = extractRequestLine(rawRequest);
        String[] split = requestLine.split(" ");

        if (split.length != 3) {
            throw new IllegalArgumentException("잘못된 Http request 입니다: " + requestLine);
        }

        return new MyHttpRequest(
                Method.valueOf(split[0]),
                split[1],
                extractResourcePath(split[1]),
                contentTypeOf(split[1]),
                split[2],
                extractCookie(rawRequest),
                extractBody(rawRequest)
        );
    }

    public Session getSession(boolean create) throws IOException {
        final String jSessionId = "JSESSIONID";
        if (this.session != null) {
            return this.session;
        }
        if (cookie.has(jSessionId)) {
            Session existsingSession = manager.findSession(cookie.getValue(jSessionId).get());
            if (existsingSession != null) {
                return existsingSession;
            }
        }
        if (!create) {
            return null;
        }

        this.session = manager.createSession();
        this.isNewSession = true;
        manager.add(this.session);
        return this.session;
    }

    private static HttpCookie extractCookie(String rawRequest) {
        return rawRequest.lines()
                .filter(line -> line.startsWith("Cookie: "))
                .map(line -> line.substring("Cookie: ".length()))
                .map(HttpCookie::from)
                .findFirst()
                .orElse(HttpCookie.from(""));
    }

    private static String extractBody(String rawRequest) {
        final String bodySeparator = "\r\n\r\n";
        int startIndexOfBody = rawRequest.indexOf(bodySeparator);
        if (startIndexOfBody == -1) {
            return "";
        }
        return rawRequest.substring(startIndexOfBody + bodySeparator.length());
    }

    public boolean hasCookie(String cookieKeyName) {
        return cookie.has(cookieKeyName);
    }

    public boolean hasRequestBody() {
        return !body.isEmpty();
    }

    public boolean hasQueryParameter() {
        return uri.contains("?");
    }

    public Map<String, String> queryParameters() {
        Map<String, String> params = new HashMap<>();
        String queryParams = uri.split("\\?")[1];
        for (String queryParam : queryParams.split("&")) {
            String[] keyValue = queryParam.split("=");
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private static String extractRequestLine(String rawRequest) {
        return rawRequest.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("http 요청을 읽을 수 없습니다."));
    }

    private static String extractResourcePath(String uri) {
        int startIndexOfPath = uri.indexOf("/");
        int startIndexOfQueryString = uri.indexOf("?");

        if (startIndexOfQueryString == -1) {
            return uri.substring(startIndexOfPath);
        }
        return uri.substring(startIndexOfPath, startIndexOfQueryString);
    }

    private static ContentType contentTypeOf(String url) {
        int lastDotIndex = url.lastIndexOf(".");
        String fileNameExtension = url.substring(lastDotIndex + 1);
        return switch (fileNameExtension) {
            case "/", "html" -> ContentType.HTML;
            case "css" -> ContentType.CSS;
            case "js" -> ContentType.JAVASCRIPT;
            case "ico" -> ContentType.ICO;
            default -> ContentType.HTML;
        };
    }

    private String resolveResourcePath(String resourcePath) {
        if (isRootPath(resourcePath) || hasExtension(resourcePath)) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }

    private static boolean isRootPath(String resourcePath) {
        return resourcePath.equals("/");
    }

    private boolean hasExtension(String resourcePath) {
        String fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0
                && dotIndex != fileName.length() - 1;
    }

    public boolean isNewSession() {
        return isNewSession;
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getVersion() {
        return version;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getBody() {
        return body;
    }
}
