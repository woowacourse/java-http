package nextstep.jwp.servlet.handler;

import org.apache.coyote.HttpResponse;
import org.apache.coyote.support.HttpException;

public abstract class ExceptionHandler {

    public abstract void handle(HttpException exception, HttpResponse response);
}
