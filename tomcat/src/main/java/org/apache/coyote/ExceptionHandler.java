package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface ExceptionHandler {

    HttpResponse handle(final Exception e) throws IOException, URISyntaxException;
}
