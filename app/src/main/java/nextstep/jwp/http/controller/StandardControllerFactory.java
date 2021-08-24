package nextstep.jwp.http.controller;

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
