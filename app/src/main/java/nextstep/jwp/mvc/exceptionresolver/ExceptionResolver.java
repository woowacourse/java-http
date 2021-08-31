package nextstep.jwp.mvc.exceptionresolver;

import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface ExceptionResolver {
    void resolve(Exception exception, HttpRequest httpRequest, HttpResponse httpResponse)
            throws NoFileExistsException;
    boolean supportsException(Exception exception);
}
