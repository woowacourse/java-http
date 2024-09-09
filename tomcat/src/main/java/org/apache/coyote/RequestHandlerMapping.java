package org.apache.coyote;

import java.util.List;
import org.apache.ResourceReader;
import org.apache.coyote.handler.LoginRequestHandler;
import org.apache.coyote.handler.NotFoundHandler;
import org.apache.coyote.handler.RootRequestHandler;
import org.apache.coyote.handler.SignupRequestHandler;
import org.apache.coyote.handler.StaticResourceRequestHandler;
import org.apache.coyote.http11.HttpMethod;

public class RequestHandlerMapping {

    private static final List<HttpMethod> LOGIN_ALLOWED_METHODS = List.of(HttpMethod.POST, HttpMethod.GET);

    private static final List<HttpMethod> REGISTER_ALLOWED_METHODS = List.of(HttpMethod.POST, HttpMethod.GET);

    public RequestHandler getRequestHandler(HttpRequest httpRequest) {

        if (httpRequest.getPath().equals("/login") && (LOGIN_ALLOWED_METHODS.contains(httpRequest.getMethod()))) {
            return new LoginRequestHandler();
        }

        if (httpRequest.getPath().equals("/register") && (REGISTER_ALLOWED_METHODS.contains(httpRequest.getMethod()))) {
            return new SignupRequestHandler();
        }

        if ("/".equals(httpRequest.getRequestURI())) {
            return new RootRequestHandler();
        }

        if (ResourceReader.canRead(httpRequest.getRequestURI()) && HttpMethod.GET.equals(httpRequest.getMethod())) {
            return new StaticResourceRequestHandler();
        }

        return new NotFoundHandler();
    }
}
