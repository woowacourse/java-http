package org.apache.coyote.http11.executor;

import com.techcourse.controller.Controller;
import com.techcourse.controller.ResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.Map;
import java.util.Optional;

public class ControllerExecutor {
    private final RequestMapper mapper;
    private final ResourceController resourceController;

    public ControllerExecutor(final Map<String, Controller> controllers) {
        this.mapper = new RequestMapper(controllers);
        this.resourceController = new ResourceController();
    }

    public void execute(final HttpRequest req, final HttpResponse res) {
        Optional.ofNullable(mapper.mappingWithController(req))
                .ifPresentOrElse(controller -> controller.service(req, res),
                        () -> resourceController.service(req, res));
    }
}

