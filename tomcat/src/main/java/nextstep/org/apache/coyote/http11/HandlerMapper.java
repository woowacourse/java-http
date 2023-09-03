package nextstep.org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.LoginController;

public class HandlerMapper {

    private static final Map<String, Object> handlerMapper = new HashMap<>();

    static {
        LoginController loginController = new LoginController();
        handlerMapper.put("/login", loginController);
    }

    public Object mapHandler(String requestedUrl) {
        return handlerMapper.get(requestedUrl);
    }
}
