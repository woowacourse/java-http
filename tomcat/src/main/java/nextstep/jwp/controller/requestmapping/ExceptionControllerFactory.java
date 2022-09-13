package nextstep.jwp.controller.requestmapping;

import java.util.List;
import nextstep.jwp.controller.InternalServerExceptionController;
import nextstep.jwp.controller.NotFoundExceptionController;
import org.apache.coyote.ExceptionController;

public class ExceptionControllerFactory {

    public static List<ExceptionController> createExceptionControllers() {
        return List.of(
                new InternalServerExceptionController(), new NotFoundExceptionController()
        );
    }
}
