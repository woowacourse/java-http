package nextstep.jwp.handler;

import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpStatus;

public class RegisterController implements Controller {

    @Override
    public boolean mapping(String method, RequestUriPath uriPath) {
        if (uriPath.getPath().equalsIgnoreCase("/register") && method.equalsIgnoreCase("get")) {
            return true;
        }
        return false;
    }

    @Override
    public ModelAndView service(String method, RequestUriPath uriPath) {
        if (uriPath.getPath().equalsIgnoreCase("/register") && method.equalsIgnoreCase("get")) {
            return new ModelAndView(Model.of(HttpStatus.OK), "/register.html");
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }
}
