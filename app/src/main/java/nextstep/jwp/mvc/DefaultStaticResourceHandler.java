package nextstep.jwp.mvc;

import static nextstep.jwp.webserver.response.ContentType.*;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.mvc.exception.PageNotFoundException;
import nextstep.jwp.webserver.exception.NoFileExistsException;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.ContentType;
import nextstep.jwp.webserver.response.HttpResponse;
import nextstep.jwp.webserver.response.StatusCode;

public class DefaultStaticResourceHandler implements StaticResourceHandler {

    private static final String DEFAULT_PATH = "static";

    @Override
    public void handleResource(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            final String file = httpRequest.httpUrl();
            httpResponse.addPage(DEFAULT_PATH + file);
            httpResponse.addHeader(contentTypeKey(), contentType(file));
            httpResponse.addStatus(StatusCode.OK);
            httpResponse.flush();
        } catch (NoFileExistsException e) {
            throw new PageNotFoundException();
        }
    }
}
