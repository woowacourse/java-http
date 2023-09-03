package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public abstract class Handler {

    abstract HttpResponse handle(HttpRequest request) throws IOException;
}
