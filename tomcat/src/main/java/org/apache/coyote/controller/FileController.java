package org.apache.coyote.controller;

import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;
import org.apache.coyote.http11.http.util.ReasonPhrase;

import java.io.IOException;

import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_TYPE;

public class FileController extends AbstractController {

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String uri = request.getUri();
        final String messageBody = getResponseBody(uri);

        response.setReasonPhrase(ReasonPhrase.OK);
        response.setMessageHeaders(CONTENT_LENGTH, Integer.toString(messageBody.getBytes().length));
        response.setMessageHeaders(CONTENT_TYPE, Extension.findExtension(uri).getContentType());
        response.setMessageBody(messageBody);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
    }

    private String getResponseBody(final String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        return FileResolver.readFileToString(uri);
    }
}
