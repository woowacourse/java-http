package nextstep.jwp.mvc.exceptionresolver;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;
import nextstep.jwp.webserver.response.StatusCode;

public class ExceptionResolverContainer {

    private List<ExceptionResolver> exceptionResolvers;

    public ExceptionResolverContainer() {
        this.exceptionResolvers = defaultExceptionResolvers();
    }

    private List<ExceptionResolver> defaultExceptionResolvers() {
        List<ExceptionResolver> exceptionResolvers = new ArrayList<>();
        exceptionResolvers.add(new NotFoundExceptionResolver());
        return exceptionResolvers;
    }

    public void resolve(Exception e, HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            for (ExceptionResolver exceptionResolver : exceptionResolvers) {
                if(exceptionResolver.supportsException(e)) {
                    exceptionResolver.resolve(e, httpRequest, httpResponse);
                    return;
                }
            }
            serverError(httpResponse);
        } catch (NoFileExistsException noFileExistsException) {
            serverError(httpResponse);
        }
    }

    private void serverError(HttpResponse httpResponse) {
        try {
            httpResponse.addStatus(StatusCode.SERVER_ERROR);
            httpResponse.addPage("static/500.html");
            httpResponse.flush();
        } catch (NoFileExistsException e) {
            throw new IllegalStateException("unknown exception");
        }
    }
}
