package org.apache.controller;

import org.apache.common.HttpRequest;
import org.apache.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.apache.common.StatusCode.NOT_FOUND;

public class NotFoundController extends AbstractController{
    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setVersion("HTTP/1.1");
        httpResponse.setStatusCode(NOT_FOUND);
    }
}
