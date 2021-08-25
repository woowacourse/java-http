package nextstep.jwp.server.handler.controller.standard;

import java.util.List;

public class StandardControllerFactory {
    public static List<StandardController> create() {
        return List.of(
            new GetStandardController(),
            new DeleteStandardController(),
            new PostStandardController(),
            new PutStandardController()
        );
    }
}
