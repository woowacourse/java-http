package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;

public interface Handler {

    HttpResponse resolve(HttpRequest request) throws IOException;
}
