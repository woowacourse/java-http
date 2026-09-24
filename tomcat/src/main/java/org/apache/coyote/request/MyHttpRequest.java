package org.apache.coyote.request;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.EntityHeader;
import org.apache.coyote.GeneralHeader;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.ContentType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyHttpRequest {

    private SessionManager manager = SessionManager.getInstance();
    private Session session;
    private boolean isNewSession;
    private RequestLine requestLine;
    private Map<String, Object> headerFields = new LinkedHashMap<>();
    private HttpCookie cookie;
    private String body;


    public MyHttpRequest(RequestLine requestLine, Map<String, Object> headerFields, String body) {
        this.requestLine = requestLine;
        setHeaders(headerFields);
        String cookieString = headerFields.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("Cookie"))
                .map(Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining("; "));
        cookie = HttpCookie.from(cookieString);

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
        manager.add(this.session);
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

    public boolean isPath(String path) {
        return path.equals(path);
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public Optional<String> getCookie(String name) {
        return cookie.getValue(name);
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

    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }

    public boolean isStaticResourcePath(String staticResourcePath) {
        return getResourcePath().contains(staticResourcePath);
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
            String[] keyValue = parameter.split("=", 3);
            params.put(keyValue[0], keyValue[1]);
        }
        return Map.copyOf(params);
    }

    /**
     * RFC 2616에서 good practice로 언급한 순서를 따르도록 세팅한다.
     * @param headers 순서가 보장되지 않는 헤더 목록
     */
    private void setHeaders(Map<String, Object> headers) {
        for (GeneralHeader headerField : GeneralHeader.values()) {
            if (headers.containsKey(headerField.name())) {
                headerFields.put(headerField.name(), headers.remove(headerField.name()));
            }
        }

        for (RequestHeader headerField : RequestHeader.values()) {
            Object fieldValue = null;
            if (headers.containsKey(headerField.name())) {
                fieldValue = headers.remove(headerField.name());
                headerFields.put(headerField.name(), fieldValue);
            }
            if (headerField.name().equalsIgnoreCase("Cookie")) {
                cookie.add(headerField.name(), (String) fieldValue);
            }
        }

        for (EntityHeader headerField : EntityHeader.values()) {
            if (headers.containsKey(headerField.name())) {
                headerFields.put(headerField.name(), headers.remove(headerField.name()));
            }
        }

        headerFields.putAll(headers);
    }
}
