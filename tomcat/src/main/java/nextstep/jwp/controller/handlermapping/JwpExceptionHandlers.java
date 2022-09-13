package nextstep.jwp.controller.handlermapping;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.InternalServerExceptionHandler;
import nextstep.jwp.controller.NotFoundExceptionHandler;
import org.apache.catalina.ExceptionHandlers;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.ExceptionHandler;

public class JwpExceptionHandlers implements ExceptionHandlers {

    private static final List<ExceptionHandler> EXCEPTION_HANDLERS = new ArrayList<>();
    private static final ExceptionHandlers INSTANCE = new JwpExceptionHandlers();

    static {
        EXCEPTION_HANDLERS.addAll(List.of(
                new InternalServerExceptionHandler(),
                new NotFoundExceptionHandler()
        ));
    }

    private JwpExceptionHandlers() {
    }

    public static ExceptionHandlers getInstance() {
        return INSTANCE;
    }

    @Override
    public ExceptionHandler find(final Exception exception) {
        return EXCEPTION_HANDLERS.stream()
                .filter(handler -> handler.isResolvable(exception))
                .findFirst()
                .orElseThrow(ControllerNotFoundException::new);
    }
}
