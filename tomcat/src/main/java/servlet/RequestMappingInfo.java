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

    public RequestMappingInfo match(Request request) {
        if (path.equals(request.getPath()) && httpMethod.equals(request.getHttpMethod())) {
            return this;
        }
        return null;
    }

    public Handler getHandler() {
        return this.handler;
    }
}
