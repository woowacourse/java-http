package org.apache.catalina.container;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.servlet.Servlet;
import com.techcourse.servlet_impl.HelloWorldServlet;
import com.techcourse.servlet_impl.LoginServlet;
import com.techcourse.servlet_impl.static_resource.*;

import java.util.List;

public class SimpleContainer implements Container {

    // TODO : 리플렉션/클래스패스로더 등으로 실제 스캔하여 구현
    private static final List<Servlet> servlets = List.of(new LoginServlet(), new LoginPageServlet(), new HelloWorldServlet(), new IndexPageServlet(), new CssServlet(), new ChartAreaJsServlet(), new ChartBarJsServlet(), new ChartPieJsServlet(), new ScriptsJsServlet());

    @Override
    public HttpResponse service(HttpRequest request) {
        Servlet servlet = findServletFor(request);
        return servlet.handle(request);
    }

    private static Servlet findServletFor(HttpRequest request) {
        // TODO : 명확한 예외 타입 사용
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(request))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
