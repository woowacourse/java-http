package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.ExceptionController;

public class ExceptionControllerFactory {

    public static List<ExceptionController> createExceptionControllers() {
        return List.of(
                new InternalServerExceptionController(), new NotFoundExceptionController()
        );
    }
}
