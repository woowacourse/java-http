package nextstep.jwp.controller;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
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

           return getMapping(uri);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            for (Method method : Controller.class.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostMapping.class)) {
                    String path = method.getAnnotation(PostMapping.class).path();
                    if(path.equals(uri)){
                        method.setAccessible(true);
                        return (String) method.invoke(controller, uri, body);
                    }
                }
            }
        }

        throw new HttpException("설정되어 있지 않은 http 메소드입니다.");
    }

    private String getMapping(String uri) throws IOException {
        if ("/".equals(uri)) {
            return ResponseEntity
                    .responseBody("Hello world!")
                    .build();
        }

        if (requestLine.isQueryString()) {
            Map<String, String> params = requestLine.getParams();
            if (service.isAuthorized(params)) {
                return ResponseEntity
                        .statusCode(StatusCode.FOUND)
                        .responseResource("/index.html")
                        .build();
            }
            return ResponseEntity
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        }

        return ResponseEntity
                .responseResource(uri)
                .build();
    }

}
