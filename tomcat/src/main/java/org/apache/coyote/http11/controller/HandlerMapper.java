package org.apache.coyote.http11.controller;

import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum HandlerMapper {
    LOGIN_CONTROLLER(uri -> LoginController.getInstance().canHandle(uri), LoginController.getInstance()),
    USER_CONTROLLER(uri -> new UserController().canHandle(uri), new UserController()),
    ;

    private Predicate<String> condition;
    private Controller controller;

    HandlerMapper(Predicate<String> condition, Controller controller) {
        this.condition = condition;
        this.controller = controller;
    }

    public static Controller mapTo(String uri) {
        return Stream.of(values())
                .filter(controller -> controller.condition.test(uri))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(uri + " 을 처리할 수 있는 핸들러가 없습니다."))
                .controller;
    }

    public static boolean hasHandler(String uri) {
        return Stream.of(values())
                .anyMatch(controller -> controller.condition.test(uri));
    }
}
