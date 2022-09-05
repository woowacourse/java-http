package org.apache.coyote.http11.response.generator;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface ResponseGenerator {

    boolean isSuitable(HttpRequest httpRequest);

    HttpResponse generate(HttpRequest httpRequest) throws IOException;
}
