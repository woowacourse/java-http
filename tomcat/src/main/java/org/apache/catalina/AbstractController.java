package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.FileExtractor;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException();
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws IOException;

    protected void setResponse(
            final HttpResponse response,
            final String resource,
            final HttpStatusCode statusCode
    ) throws IOException {
        final ResponseBody responseBody = FileExtractor.extractFile(resource);
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);

        response.setStatusLine(new StatusLine(statusCode));
        response.setResponseHeader(responseHeader);
        response.setResponseBody(responseBody);
    }
}
