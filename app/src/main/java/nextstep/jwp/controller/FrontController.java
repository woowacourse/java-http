package nextstep.jwp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.service.HttpService;

public class FrontController {
    private final RequestLine requestLine;
    private final HttpService service;
    private final RequestBody body;

    public FrontController(RequestBody body, RequestLine requestLine) {
        this.requestLine = requestLine;
        this.body = body;
        this.service = new HttpService();
    }

    public String response() throws IOException, InvocationTargetException, IllegalAccessException {
        final String httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        Controller controller = new Controller();
        if (HttpMethod.GET.equals(httpMethod)) {
            Set<String> getMappings = Arrays.stream(Controller.class.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(GetMapping.class))
                    .map(method -> method.getAnnotation(GetMapping.class).path())
                    .collect(Collectors.toSet());
            if (!getMappings.contains(uri)) {
                return ResponseEntity
                        .responseResource(uri)
                        .build();
            }
            for (Method method : Controller.class.getDeclaredMethods()) {
                if (matchGetMapping(method, uri)) {
                    method.setAccessible(true);
                    return (String) method.invoke(controller, uri);
                }
            }
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            for (Method method : Controller.class.getDeclaredMethods()) {
                if (matchPostMapping(method, uri)) {
                    method.setAccessible(true);
                    return (String) method.invoke(controller, uri, body);
                }
            }
        }

        throw new HttpException("설정되어 있지 않은 http 메소드입니다.");
    }

    private boolean matchGetMapping(Method method, String uri) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            String path = method.getAnnotation(GetMapping.class).path();
            return path.equals(uri);
        }
        return false;
    }

    private boolean matchPostMapping(Method method, String uri) {
        if (method.isAnnotationPresent(PostMapping.class)) {
            String path = method.getAnnotation(PostMapping.class).path();
            return path.equals(uri);
        }
        return false;
    }

}
