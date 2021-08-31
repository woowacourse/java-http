package nextstep.jwp.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;

public class FrontController {
    private final RequestLine requestLine;
    private final RequestBody body;

    public FrontController(RequestBody body, RequestLine requestLine) {
        this.requestLine = requestLine;
        this.body = body;
    }

    public String response() throws Exception {
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        Controller controller = new Controller();
        if (HttpMethod.GET == httpMethod) {
            return getMapping(uri, controller);
        }
        if (HttpMethod.POST == httpMethod) {
            return postMapping(uri, controller);
        }

        throw new HttpException("설정되어 있지 않은 http 메소드입니다.");
    }

    private String postMapping(String uri, Controller controller) throws Exception {
        for (Method method : Controller.class.getDeclaredMethods()) {
            if (matchPostMapping(method, uri)) {
                return (String) method.invoke(controller, body);
            }
        }
        throw new HttpException("잘못된 http post 요청입니다.");
    }

    private String getMapping(String uri, Controller controller) throws Exception {
        for (Method method : Controller.class.getDeclaredMethods()) {
            if (matchGetMapping(method, uri)) {
                return (String) method.invoke(controller);
            }
        }

        Set<String> declaredGetMappings = collectDeclaredGetMappings();
        if (!declaredGetMappings.contains(uri)) {
            return ResponseEntity
                    .responseResource(uri)
                    .build();
        }
        throw new HttpException("잘못된 http get 요청입니다.");
    }

    private Set<String> collectDeclaredGetMappings() {
        return Arrays.stream(Controller.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .map(method -> method.getAnnotation(GetMapping.class).path())
                .collect(Collectors.toSet());
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
