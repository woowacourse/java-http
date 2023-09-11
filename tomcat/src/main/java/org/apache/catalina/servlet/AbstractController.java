package org.apache.catalina.servlet;

import org.apache.catalina.exception.MethodNotSupportedException;
import org.apache.catalina.util.ResourceFileReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            if (request.isRequestMethodOf(HttpMethod.GET)) {
                doGet(request, response);
                return;
            }

            if (request.isRequestMethodOf(HttpMethod.POST)) {
                doPost(request, response);
            }
        } catch (MethodNotSupportedException e) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED)
                    .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                    .setBody(ResourceFileReader.readFile("/405.html"));
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotSupportedException();
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new MethodNotSupportedException();
    }
}
