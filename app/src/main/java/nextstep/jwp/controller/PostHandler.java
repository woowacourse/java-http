package nextstep.jwp.controller;

import java.lang.reflect.Method;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.request.RequestBody;

public class PostHandler implements Handler {
    private final HttpMethod httpMethod = HttpMethod.POST;
    private final RequestBody requestBody;

    public PostHandler(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public boolean matchHttpMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public String runController(String uri, Controller controller) throws Exception {
        for (Method method : Controller.class.getDeclaredMethods()) {
            if (matchPostMapping(method, uri)) {
                return (String) method.invoke(controller, requestBody);
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
