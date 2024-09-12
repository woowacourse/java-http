package org.apache.catalina.controller;

import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;

public class NotFoundController extends AbstractController {

    private static final String ERROR_401_PATH = "/401.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse.HttpResponseBuilder response) throws Exception {
        buildRedirectResponse(ERROR_401_PATH, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse.HttpResponseBuilder response) {
        throw new RuntimeException();
    }

    private void buildRedirectResponse(String location, HttpResponse.HttpResponseBuilder response) {
        response.withStatusCode(StatusCode.FOUND)
                .addHeader(HttpHeader.LOCATION.getValue(), location);
    }
}
