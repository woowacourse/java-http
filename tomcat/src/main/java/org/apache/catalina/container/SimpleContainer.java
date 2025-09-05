package org.apache.catalina.container;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.servlet.Servlet;
import com.java.servlet.StaticResourceRegistryServlet;
import com.techcourse.servlet_impl.HelloWorldServlet;
import com.techcourse.servlet_impl.LoginServlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleContainer implements Container {

    // TODO : 리플렉션/클래스패스로더 등으로 실제 스캔하여 구현
    private static final List<Servlet> servlets = new ArrayList<>();

    static {
        servlets.add(new LoginServlet());
        servlets.add(new HelloWorldServlet());
        servlets.add(new StaticResourceRegistryServlet()
                .register("/assets/chart-area.js", "static/assets/chart-area.js")
                .register("/assets/chart-bar.js", "static/assets/chart-bar.js")
                .register("/assets/chart-pie.js", "static/assets/chart-pie.js")
                .register("/css/styles.css", "static/css/styles.css")
                .register("/favicon.ico", "static/favicon.ico")
                .register("/index.html", "static/index.html")
                .register("/login.html", "static/login.html")
                .register("/js/scripts.js", "static/js/scripts.js")
        );
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Optional<Servlet> servlet = findServletFor(request);
        if (servlet.isPresent()) {
            try {
                return servlet.get().handle(request);
            } catch (Exception e) {
                return HttpResponse.internalServerError(e);
            }
        } else {
            return HttpResponse.notFound("해당 요청을 처리할 서블릿을 찾지 못했습니다. uri=" + request.uri());
        }
    }

    private static Optional<Servlet> findServletFor(HttpRequest request) {
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(request))
                .findFirst();
    }
}
