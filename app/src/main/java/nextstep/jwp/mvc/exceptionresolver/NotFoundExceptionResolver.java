package nextstep.jwp.mvc.exceptionresolver;

import nextstep.jwp.mvc.exception.PageNotFoundException;
import nextstep.jwp.webserver.response.StatusCode;

public class NotFoundExceptionResolver extends AbstractExceptionResolver{

    @Override
    public boolean supportsException(Exception exception) {
        return exception.getClass().isAssignableFrom(PageNotFoundException.class);
    }

    @Override
    protected String pageName() {
        return "static/404.html";
    }

    @Override
    protected StatusCode statusCode() {
        return StatusCode.NOT_FOUND;
    }
}
