package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class NotFoundController implements Controller {

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest)
            throws URISyntaxException, IOException {
        return HttpResponse.notFound();
    }
}
