package nextstep.jwp.model;

import java.io.IOException;
import java.util.Map;

public class Request {

    private String requestMethod;
    private RequestPath requestPath;
    private Map<String, String> requestHeaders;
    private RequestBody requestBody;

    public Request(String requestMethod, Map<String, String> requestHeaders,
                   RequestPath requestPath, RequestBody requestBody) {
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.requestPath = requestPath;
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public boolean isPath(PathType pathType) {
        return requestPath.isPath(pathType);
    }

    public boolean containsPath(PathType pathType) {
        return requestPath.containsPath(pathType);
    }

    public boolean containsEXTENSION() {
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

    public Map<String, String> queries() throws IOException {
        return requestPath.queries();
    }
}
