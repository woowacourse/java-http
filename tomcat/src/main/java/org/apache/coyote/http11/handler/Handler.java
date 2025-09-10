package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.dto.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {

    boolean canHandle(HttpRequest request);

    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
