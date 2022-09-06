package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public interface Controller {

    boolean isRunnable(final Request request);
    void run(final Request request, final Response response) throws IOException, URISyntaxException;
}
