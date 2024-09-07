package com.techcourse.controller;

import com.techcourse.exception.client.NotFoundException;
import java.util.Arrays;

public enum ControllerMapper {

    MAIN("/", new MainPageController()),
    LOGIN("/login", new LoginController()),
    FILE(null, new FileController()),
    ;

    private final String path;
    private final Controller controller;

    ControllerMapper(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller findByPath(String path) {
        if (path.endsWith("html") || path.endsWith("css") || path.endsWith("js")) {
            return FILE.controller;
        } else {
            return Arrays.stream(values())
                    .filter(controllerMapper -> controllerMapper.path.equals(path))
                    .findAny()
                    .map(ControllerMapper::getController)
                    .orElseThrow(() -> new NotFoundException("리소스를 처리할 컨트롤러가 없습니다."));
        }
    }

    public Controller getController() {
        return controller;
    }
}
