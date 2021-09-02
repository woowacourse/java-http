package nextstep.jwp.mvc.exceptionresolver;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;
import nextstep.jwp.webserver.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionResolverContainer {
    private static final Logger log = LoggerFactory.getLogger(ExceptionResolverContainer.class);


    private final List<ExceptionResolver> exceptionResolvers;

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
            serverError(httpResponse, e);
        } catch (NoFileExistsException noFileExistsException) {
            serverError(httpResponse, noFileExistsException);
        }
    }

    private void serverError(HttpResponse httpResponse, Exception exception) {
        try {
            httpResponse.addStatus(StatusCode.SERVER_ERROR);
            httpResponse.addPage("static/500.html");
            httpResponse.flush();
            exception.printStackTrace();
        } catch (NoFileExistsException e) {
            throw new IllegalStateException("unknown exception");
        }
    }
}
