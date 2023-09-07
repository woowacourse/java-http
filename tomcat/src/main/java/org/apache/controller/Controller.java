package org.apache.controller;

import org.apache.common.HttpRequest;
import org.apache.common.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException;

}
