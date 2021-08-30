package nextstep.jwp.tomcat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.servlet.StaticResourceServlet;
import nextstep.jwp.servlet.UserLoginServlet;
import nextstep.jwp.servlet.UserRegisterServlet;

public class ServletContainer {

    private static final List<Servlet> SERVLETS = new LinkedList<>();
    private static final StaticResourceServlet STATIC_RESOURCE_SERVLET = new StaticResourceServlet();

    static {
        SERVLETS.add(new UserLoginServlet());
        SERVLETS.add(new UserRegisterServlet());
    }

    public void process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (isStaticResourceRequest(httpRequest)) {
            STATIC_RESOURCE_SERVLET.service(httpRequest, httpResponse);
            return;
        }

        Servlet servlet = findServletByRequestMapping(httpRequest.getUri());
        servlet.service(httpRequest, httpResponse);
    }

    private Servlet findServletByRequestMapping(String requestMapping) {
        return SERVLETS.stream()
            .filter(it -> it.isSameRequestMapping(requestMapping))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }


    public boolean isStaticResourceRequest(HttpRequest httpRequest) {
        String uri = httpRequest.getUri();

        return httpRequest.getBody().isEmpty()
            && (!Objects.isNull(getClass().getResource("/static/" + uri))
            || !Objects.isNull(getClass().getResource("/static/" + uri + ".html")));
    }
}
