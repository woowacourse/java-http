package org.apache.catalina.servlet;

import org.apache.catalina.exception.UncheckedServletException;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;

public class DefaultServlet extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            final Url url = request.getUrl();
            final String body = ResourceFileReader.readFile(url.getUrlPath());

            sendStaticFileResponse(request, response, HttpStatus.OK, body);
        } catch (UncheckedServletException e) {
            final String body = ResourceFileReader.readFile("/404.html");
            sendStaticFileResponse(request, response, HttpStatus.NOT_FOUND, body);
        }
    }

    private void sendStaticFileResponse(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse,
            final HttpStatus status,
            final String body
    ) {
        httpResponse.setStatus(status)
                .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.getContentType(httpRequest))
                .setBody(body);
    }

    @Override
    public boolean isSupported(final HttpRequest httpRequest) {
        return httpRequest.isRequestMethodOf(HttpMethod.GET) &&
                SupportFile.isSupportFileExtension(httpRequest);
    }
}
