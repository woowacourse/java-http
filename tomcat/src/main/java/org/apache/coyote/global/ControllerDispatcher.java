package org.apache.coyote.global;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.AbstractController;
import org.apache.coyote.controller.ViewController;
import org.apache.coyote.global.exception.URIUnmappedException;
import org.apache.coyote.controller.MemberController;
import org.apache.coyote.http11.common.HttpMethod;

public class ControllerDispatcher {

    private static final Map<String, AbstractController> dispatchers = new HashMap<>();
    private static final MemberController CONTROLLER_MEMBER = new MemberController();
    public static final ViewController CONTROLLER_VIEW = new ViewController();

    static {
        dispatchers.put("/", CONTROLLER_VIEW);
        dispatchers.put("/login", CONTROLLER_MEMBER);
        dispatchers.put("/register", CONTROLLER_MEMBER);
    }

    private ControllerDispatcher() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 생성할 수 없습니다.");
    }

    public static AbstractController dispatch(HttpMethod method, String uriPath) {
        if (uriPath.contains(".") || method == HttpMethod.GET) {
            return CONTROLLER_VIEW;
        }

        AbstractController selectedController = dispatchers.get("/" + uriPath);
        if (selectedController == null) {
            throw new URIUnmappedException(uriPath);
        }
        return selectedController;
    }
}
