package nextstep.jwp.controller.other;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.page.NotFoundController;
import org.apache.coyote.http11.request.HttpRequest;

public enum OtherControllerMapping {

    OTHER(OtherController.create());

    private final Controller controller;

    OtherControllerMapping(final Controller controller) {
        this.controller = controller;
    }

    public static Controller find(final HttpRequest httpRequest) {
        return Arrays.stream(OtherControllerMapping.values())
                .map(value -> value.controller)
                .findFirst()
                .orElse(NotFoundController.create());
    }
}
