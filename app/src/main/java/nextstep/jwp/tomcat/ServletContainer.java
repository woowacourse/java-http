package nextstep.jwp.tomcat;

import static nextstep.jwp.http.reponse.HttpResponse.STATIC_RESOURCE_PATH;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.http.reponse.HttpResponse;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.servlet.StaticResourceServlet;
import nextstep.jwp.servlet.UserLoginServlet;
import nextstep.jwp.servlet.UserRegisterServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {

    private static final Logger log = LoggerFactory.getLogger(ServletContainer.class);
    private static final List<Servlet> SERVLETS = new ArrayList<>();
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

        try {
            Servlet servlet = findServletByRequestURI(httpRequest.getRequestURI());
            servlet.service(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error("uri에 매핑되는 서블릿이 존재하지 않습니다. 에러 메세지");
            httpResponse.sendRedirect("/500.html");
        }
    }

    private Servlet findServletByRequestURI(String requestMapping) {
        return SERVLETS.stream()
            .filter(it -> it.isSameRequestMapping(requestMapping))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }


    public boolean isStaticResourceRequest(HttpRequest httpRequest) {
        URL resource = getClass().getResource(STATIC_RESOURCE_PATH + httpRequest.getRequestURI());
        return !Objects.isNull(resource);
    }
}
