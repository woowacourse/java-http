package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public interface Controller {

    boolean isRunnable(final Http11Request request);
    void run(final Http11Request request, final Http11Response response) throws IOException, URISyntaxException;
}
