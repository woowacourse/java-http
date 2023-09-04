package org.apache.coyote.http11.handle.view;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface ViewHandler {

    HttpResponse handle(HttpRequest request) throws IOException;
}
