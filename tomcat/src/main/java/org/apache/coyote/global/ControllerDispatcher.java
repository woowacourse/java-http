package org.apache.coyote.global;

import com.techcourse.controller.AbstractController;
import com.techcourse.controller.FrontViewController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.global.exception.URIUnmappedException;
import org.apache.coyote.http11.HttpRequest;

public class ControllerDispatcher {

    public static final FrontViewController CONTROLLER_VIEW = new FrontViewController();
    private static final LoginController CONTROLLER_MEMBER = new LoginController();
    private static final RegisterController CONTROLLER_REGISTER = new RegisterController();
    private static final Map<String, AbstractController> dispatchers = new HashMap<>();

    static {
        dispatchers.put("/", CONTROLLER_VIEW);
        dispatchers.put("/login", CONTROLLER_MEMBER);
        dispatchers.put("/register", CONTROLLER_REGISTER);
    }

    private ControllerDispatcher() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static AbstractController dispatch(HttpRequest request) {
        String uriPath = request.getUri();
        if (uriPath.contains(".")) {
            return CONTROLLER_VIEW;
        }

        AbstractController selectedController = dispatchers.get(uriPath);
        if (selectedController == null) {
            throw new URIUnmappedException(uriPath);
        }
        return selectedController;
    }
}
