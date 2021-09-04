package nextstep.jwp.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestHeader;
import nextstep.jwp.response.HttpResponse;

public class GetHandler implements Handler {
    private final HttpMethod httpMethod = HttpMethod.GET;

    public GetHandler() {
    }

    @Override
    public boolean matchHttpMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    @Override
    public String runController(HttpRequest httpRequest, Controller controller) throws Exception {
        String uri = httpRequest.getRequestLine().getUri();
        for (Method method : Controller.class.getDeclaredMethods()) {
            if (matchGetMapping(method, uri)) {
                return (String) method.invoke(controller, httpRequest);
            }
        }

        Set<String> declaredGetMappings = collectDeclaredGetMappings();
        if (!declaredGetMappings.contains(uri)) {
            return HttpResponse
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
}
