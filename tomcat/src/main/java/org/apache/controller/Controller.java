package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface Controller {

    boolean isProcessable(HttpRequest httpRequest);

    HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse) throws URISyntaxException, IOException;
}
