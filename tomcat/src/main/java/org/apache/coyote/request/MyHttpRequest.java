package org.apache.coyote.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.EntityHeader;
import org.apache.coyote.GeneralHeader;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.ContentType;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyHttpRequest {

    private final SessionManager manager = SessionManager.getInstance();
    private Session session;
    private boolean isNewSession;
    private final RequestLine requestLine;
    private final Map<String, Object> headerFields = new LinkedHashMap<>();
    private HttpCookie cookie;
    private String body;


    public MyHttpRequest(RequestLine requestLine, Map<String, Object> headerFields, String body) {
        this.requestLine = requestLine;
        Map<String, Object> copiedHeaders = new LinkedHashMap<>(headerFields);
        String cookieString = copiedHeaders.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("Cookie"))
                .map(Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        cookie = HttpCookie.from(cookieString);
        setHeaders(copiedHeaders);

        this.body = body;
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
        return this.session;
    }

    public boolean isNewSession() {
        return isNewSession;
    }

    public Method method() {
        return requestLine.getMethod();
    }

    public boolean isGet() {
        return requestLine.hasMethod(Method.GET);
    }

    public boolean isPost() {
        return requestLine.hasMethod(Method.POST);
    }

    public String getPath() {
        return requestLine.getRequestTarget().getPath();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public Optional<String> getHeader(String name) {
        return headerFields.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Entry::getValue)
                .map(Object::toString)
                .findFirst();
    }

    public boolean hasCookie(String name) {
        return cookie.has(name);
    }

    public ContentType getContentType() {
        return contentTypeOf(requestLine.getRequestTarget().getPath());
    }

    private ContentType contentTypeOf(String url) {
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

    public String getResourcePath() {
        final String RESOURCE_PATH_PREFIX = "static";
        return RESOURCE_PATH_PREFIX + resolveResourcePath(extractResourcePath(requestLine.getRequestTarget()));
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

    private static String extractResourcePath(RequestTarget requestTarget) {
        return requestTarget.getPath();
    }


    public Map<String, String> getFormParameters() {
        Map<String, String> params = new HashMap<>();
        for (String parameter : body.split("&")) {
            int separatorIndex = parameter.indexOf('=');
            String key = parameter.substring(0, separatorIndex);
            String value = parameter.substring(separatorIndex + 1);
            params.put(decode(key), decode(value));
        }
        return Map.copyOf(params);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    /**
     * RFC 2616에서 good practice로 언급한 순서를 따르도록 세팅한다.
     * @param headers 순서가 보장되지 않는 헤더 목록
     */
    private void setHeaders(Map<String, Object> headers) {
        for (GeneralHeader headerField : GeneralHeader.values()) {
            moveHeaderToOrderedFields(headers, headerField.fieldName());
        }

        for (RequestHeader headerField : RequestHeader.values()) {
            moveHeaderToOrderedFields(headers, headerField.fieldName());
        }

        for (EntityHeader headerField : EntityHeader.values()) {
            moveHeaderToOrderedFields(headers, headerField.fieldName());
        }

        headerFields.putAll(headers);
    }

    private void moveHeaderToOrderedFields(Map<String, Object> headers, String fieldName) {
        String actualName = headers.keySet().stream()
                .filter(name -> name.equalsIgnoreCase(fieldName))
                .findFirst()
                .orElse(null);
        if (actualName != null) {
            headerFields.put(fieldName, headers.remove(actualName));
        }
    }
}
