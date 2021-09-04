package nextstep.jwp.controller;

import java.lang.reflect.Method;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestHeader;

public class PostHandler implements Handler {
    private final HttpMethod httpMethod = HttpMethod.POST;

    public PostHandler() {
    }

    @Override
    public boolean matchHttpMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    @Override
    public String runController(HttpRequest httpRequest, Controller controller) throws Exception {
        String uri = httpRequest.getRequestLine().getUri();
        for (Method method : Controller.class.getDeclaredMethods()) {
            if (matchPostMapping(method, uri)) {
                return (String) method.invoke(controller, httpRequest);
            }
        }
        throw new HttpException("잘못된 http post 요청입니다.");
    }

    private boolean matchPostMapping(Method method, String uri) {
        if (method.isAnnotationPresent(PostMapping.class)) {
            String path = method.getAnnotation(PostMapping.class).path();
            return path.equals(uri);
        }
        return false;
    }
}
