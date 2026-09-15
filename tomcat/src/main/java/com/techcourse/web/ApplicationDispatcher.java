package com.techcourse.web;

import com.techcourse.controller.ApplicationController;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Dispatcher;

public class ApplicationDispatcher implements Dispatcher {

    private final ApplicationController applicationController;

    public ApplicationDispatcher(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    @Override
    public Optional<String> dispatch(
            String path,
            Map<String, String> params
    ) {
        if ("/login".equals(path)) {
            return Optional.of(applicationController.login(params));
        }

        return Optional.empty();
    }
}
