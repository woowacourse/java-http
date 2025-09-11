package org.apache.coyote.http11.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.exception.NotFoundException;

public enum ControllerProvider {
    INSTANCE;

    private final List<Controller> controllers;

    ControllerProvider() {
        this.controllers = new ArrayList<>();
    }

    public void register(final List<Controller> controllers) {
        this.controllers.addAll(controllers);
    }

    public Controller findByPath(final String path) {
        return controllers.stream()
                .filter(controller -> controller.isProvidableUrl(path))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 path입니다: %s".formatted(path)));
    }
}
