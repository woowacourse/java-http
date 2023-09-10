package org.apache.coyote;

import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;

import java.io.IOException;

public interface Controller {

    HttpResponse run(final HttpRequest request) throws IOException;
}
