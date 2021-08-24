package nextstep.jwp.framework.infrastructure.http.mapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.infrastructure.http.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.http.adapter.StaticViewAdapter;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.util.ApplicationContextLoader;

public class HttpRequestMapping implements RequestMapping {

    private static final List<Class<?>> BEAN_CLASSESS =
        ApplicationContextLoader.loadBeans("nextstep");

    @Override
    public RequestAdapter findAdapter(HttpRequest httpRequest) {
        if (isRequestingStaticFiles(httpRequest)) {
            return searchViewAdapter(httpRequest);
        }
        return new StaticViewAdapter(null, null);
    }

    private boolean isRequestingStaticFiles(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HttpMethod.GET);
    }

    private StaticViewAdapter searchViewAdapter(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        for (Class<?> bean : BEAN_CLASSESS) {
            if (!bean.isAnnotationPresent(Controller.class)) {
                continue;
            }
            Optional<Method> target = Arrays.stream(bean.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .filter(method -> method.getAnnotation(GetMapping.class).value().equals(url))
                .findAny();
            if (target.isPresent()) {
                return new StaticViewAdapter(bean, target.orElseThrow(IllegalStateException::new));
            }
        }
        return StaticViewAdapter.notFound();
    }

//
//    private String executeGetMethod(HttpRequestStartLine httpRequestStartLine) throws IOException {
//        String url = httpRequestStartLine.getUrl();
//        if (url.equals("/") || url.equals("/index.html")) {
//            Path path = staticFilePaths.stream()
//                .filter(filePath -> filePath.toString().endsWith("/static/index.html"))
//                .findAny()
//                .orElseThrow(IllegalAccessError::new);
//            String responseBody = String.join("\r\n", Files.readAllLines(path));
//            String response = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: " + responseBody.getBytes().length + " ",
//                "",
//                responseBody);
//            return response;
//        }
//
//        if (url.equals("/login")) {
//            Path path = staticFilePaths.stream()
//                .filter(filePath -> filePath.toString().endsWith("/static/login.html"))
//                .findAny()
//                .orElseThrow(IllegalAccessError::new);
//            String responseBody = String.join("\r\n", Files.readAllLines(path));
//            String response = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: " + responseBody.getBytes().length + " ",
//                "",
//                responseBody);
//            return response;
//        }
//
//        return "";
//    }
//
//    private String executePostMethod(HttpRequestStartLine httpRequestStartLine) {
//        return null;
//    }
}
