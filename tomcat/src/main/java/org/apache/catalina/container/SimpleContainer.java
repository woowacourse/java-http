package org.apache.catalina.container;

import com.java.http.request_response.Body;
import com.java.http.request_response.HttpRequest;
import com.java.http.request_response.HttpResponse;
import com.java.servlet.Servlet;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.java.http.request_response.StatusCode.INTERNAL_SERVER_ERROR;
import static com.java.http.request_response.StatusCode.NOT_FOUND;

public class SimpleContainer implements Container {

    private static final Logger log = LoggerFactory.getLogger(SimpleContainer.class);
    // 동시 접근은 가능하지만, 수정되지 않으므로 기본 컬렉션을 사용한다.
    private static final List<Servlet> servlets = scan("com.techcourse");

    private static List<Servlet> scan(String packageName) {
        List<Servlet> servlets = new ArrayList<>();

        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> servletClasses = reflections.getTypesAnnotatedWith(TomcatServlet.class);
        for (Class<?> servletClass : servletClasses) {
            try {
                Servlet servlet = (Servlet) servletClass.getDeclaredConstructor().newInstance();
                servlets.add(servlet);
            } catch (Exception e) {
                log.warn("서블릿을 초기화에 실패했습니다. 클래스명={}", servletClass.getSimpleName());
            }
        }

        return servlets;
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Optional<Servlet> servlet = findServletFor(request);

        if (servlet.isEmpty()) {
            return HttpResponse.of(NOT_FOUND)
                    .body(Body.plaintext("해당 요청을 처리할 서블릿을 찾지 못했습니다. uri=" + request.uri()));
        }

        try {
            return servlet.get().handle(request);
        } catch (Exception e) {
            return HttpResponse.of(INTERNAL_SERVER_ERROR)
                    .body(Body.stackTrace(e));
        }
    }

    private static Optional<Servlet> findServletFor(HttpRequest request) {
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(request))
                .findFirst();
    }
}
