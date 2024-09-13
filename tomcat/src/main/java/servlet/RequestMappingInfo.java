package servlet;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.Request;
import servlet.handler.Handler;

public class RequestMappingInfo {

    private final String path;

    private final HttpMethod httpMethod;

    private final Handler handler;

    public RequestMappingInfo(String path, HttpMethod httpMethod, Handler handler) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.handler = handler;
    }

    public Handler getHandler(Request request) {
        if (matches(request)) {
            return this.handler;
        }
        return null;
    }

    private boolean matches(Request request) {
        return request.isSamePath(path) && request.isSameMethod(httpMethod);
    }
}
