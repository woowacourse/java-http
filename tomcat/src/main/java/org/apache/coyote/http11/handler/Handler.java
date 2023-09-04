package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;

public interface Handler {

    HttpResponse handle(HttpRequest request) throws IOException;
}
