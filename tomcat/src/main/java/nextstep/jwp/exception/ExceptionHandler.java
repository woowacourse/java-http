package nextstep.jwp.exception;

import java.util.Arrays;
import nextstep.jwp.servlet.view.ViewResource;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class ExceptionHandler {

    public ViewResource handle(HttpException exception) {
        final var status = exception.getStatus();
        final var path = ExceptionPage.toUri(status);
        return new ViewResource(status, path);
    }

    private enum ExceptionPage {

        BAD_REQUEST(HttpStatus.BAD_REQUEST, "/400.html"),
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "/401.html"),
        NOT_FOUND(HttpStatus.NOT_FOUND, "/404.html"),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html")
        ;

        final HttpStatus status;
        final String uri;

        ExceptionPage(HttpStatus status, String uri) {
            this.status = status;
            this.uri = uri;
        }

        static String toUri(HttpStatus status) {
            return Arrays.stream(values())
                    .filter(it -> it.status.equals(status))
                    .map(it -> it.uri)
                    .findAny()
                    .orElse(INTERNAL_SERVER_ERROR.uri);
        }
    }
}
