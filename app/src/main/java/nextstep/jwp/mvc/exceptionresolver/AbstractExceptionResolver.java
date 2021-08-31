package nextstep.jwp.mvc.exceptionresolver;

import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;
import nextstep.jwp.webserver.response.StatusCode;

public abstract class AbstractExceptionResolver implements ExceptionResolver {

    @Override
    public void resolve(Exception exception, HttpRequest httpRequest, HttpResponse httpResponse)
            throws NoFileExistsException {
        renderPage(httpResponse);
    }

    private void renderPage(HttpResponse httpResponse)
            throws NoFileExistsException {
        String pageName = pageName();
        StatusCode statusCode = statusCode();
        httpResponse.addStatus(statusCode);
        httpResponse.addPage(pageName);
        httpResponse.flush();
    }

    protected abstract String pageName();

    protected abstract StatusCode statusCode();


}
