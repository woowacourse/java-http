package org.apache.handler;

import java.io.IOException;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public interface RequestHandler {

    HttpResponse handle(HttpRequest httpRequest) throws IOException;
}
