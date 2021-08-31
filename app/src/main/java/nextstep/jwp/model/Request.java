package nextstep.jwp.model;

import java.util.Map;
import java.util.UUID;

public class Request {

    private String requestMethod;
    private RequestPath requestPath;
    private Map<String, String> requestHeaders;
    private RequestBody requestBody;
    private Session session;

    public Request(String requestMethod, Map<String, String> requestHeaders,
                   RequestPath requestPath, RequestBody requestBody) {
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.requestPath = requestPath;
        this.requestBody = requestBody;
        this.session = initializeSession();
    }

    private Session initializeSession() {
        String id = initializeSessionId();
        return Sessions.getSession(id);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public boolean hasCookieHeader() {
        return requestHeaders.containsKey("Cookie");
    }

    public Session getSession() {
        return session;
    }

    private String initializeSessionId() {
        if (requestHeaders.containsKey("Cookie")) {
            return new Cookie(requestHeaders.get("Cookie")).getSessionId();
        }
        return UUID.randomUUID().toString();
    }

    public boolean isPath(PathType pathType) {
        return requestPath.isPath(pathType);
    }

    public boolean containsPath(PathType pathType) {
        return requestPath.containsPath(pathType);
    }

    public boolean containsExtension() {
        return requestPath.containsExtension();
    }

    public FileType fileType() {
        return requestPath.fileType();
    }

    public String path() {
        return requestPath.path();
    }

    public boolean hasQueryString() {
        return requestPath.hasQueryString();
    }

    public Map<String, String> queries() {
        return requestPath.queries();
    }
}
