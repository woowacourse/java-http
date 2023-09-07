package org.apache.handler;

import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public class HomeController extends AbstractController {

    private static final String DEFAULT_RESPONSE = "Hello world!";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        httpResponse.setBody(DEFAULT_RESPONSE);
    }
}
