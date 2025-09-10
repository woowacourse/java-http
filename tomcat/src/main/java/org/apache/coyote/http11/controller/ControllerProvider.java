package org.apache.coyote.http11.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.util.List;
import org.apache.coyote.http11.exception.InternalServerErrorException;

public enum ControllerProvider {
    INSTANCE;

    private static final List<Controller> CONTROLLERS = List.of(
            new RegisterController(),
            new LoginController(),
            new StaticResourceController()
    );

    public Controller findByPath(final String path) {
        return CONTROLLERS.stream()
                .filter(controller -> controller.isProvide(path))
                .findFirst()
                .orElseThrow(() -> new InternalServerErrorException("존재하지 않는 path입니다: %s".formatted(path)));
    }
}
