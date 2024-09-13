package org.apache.coyote.http11.servlet;

import com.techcourse.servlet.DispatcherServlet;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ServletContainer {
    private static final String NOT_FOUND_FILE_PATH = "/404.html";
    private static final List<Servlet> APPLICATION_SERVLETS = List.of(new DispatcherServlet(), new StaticFileServlet());

    private final List<Servlet> servlets;

    public ServletContainer() {
        this.servlets = APPLICATION_SERVLETS;
    }

    public void service(HttpRequest request, HttpResponse response) {
        servlets.stream()
                .filter(servlet -> servlet.canService(request))
                .findFirst()
                .ifPresentOrElse(
                        servlet -> servlet.service(request, response),
                        () -> response.sendStaticResource(HttpStatus.NOT_FOUND, NOT_FOUND_FILE_PATH));
    }
}
