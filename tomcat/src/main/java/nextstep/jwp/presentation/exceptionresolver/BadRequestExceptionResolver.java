package nextstep.jwp.presentation.exceptionresolver;

import static nextstep.jwp.presentation.ResourceLocation.BAD_REQUEST;
import static org.apache.coyote.http11.util.HttpStatus.FOUND;

import customservlet.ExceptionResolver;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class BadRequestExceptionResolver implements ExceptionResolver {

    @Override
    public void resolveException(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatusCode(FOUND);
        httpResponse.setLocation(BAD_REQUEST.getLocation());
    }
}
