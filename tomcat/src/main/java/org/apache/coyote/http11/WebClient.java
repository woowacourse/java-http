package org.apache.coyote.http11;

import java.text.MessageFormat;
import org.apache.coyote.exception.BadRequestException;
import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpMethod;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpRequestUri;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.apache.coyote.support.Controller;
import org.apache.coyote.util.FileUtils;

public class WebClient {

    private static final String INDEX_URI = "/";

    public HttpResponse request(final HttpRequest httpRequest) {
        if (httpRequest.isEqualToMethod(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        throw new MethodNotAllowedException(MessageFormat.format("not allowed : {0}", httpRequest.getMethod()));
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        Controller controller = new Controller(httpRequest);

        if (httpRequest.isEqualToUri(INDEX_URI)) {
            return controller.execute();
        }

        if (httpRequest.isQueryString()) {
            return controller.execute();
        }

        return controller.execute();
    }
}
