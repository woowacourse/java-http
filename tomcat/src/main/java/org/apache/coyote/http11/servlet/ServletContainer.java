package org.apache.coyote.http11.servlet;

import com.techcourse.servlet.DispatcherServlet;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.view.View;

public class ServletContainer {
    private static final List<Servlet> APPLICATION_SERVLETS = List.of(new DispatcherServlet());

    private final List<Servlet> servlets;

    public ServletContainer() {
        this.servlets = APPLICATION_SERVLETS;
    }

    public Optional<View> service(HttpRequest request) { // TODO HttpResponse 를 반환하도록 변경
        return servlets.stream()
                .map(servlet -> servlet.service(request))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
