package nextstep.jwp.server.handler.controller.static_files;

import java.util.List;

public class StaticFilesControllerFactory {

    private StaticFilesControllerFactory() {
    }

    public static List<StaticFilesController> create() {
        return List.of(
            new GetStaticFilesController()
        );
    }
}
