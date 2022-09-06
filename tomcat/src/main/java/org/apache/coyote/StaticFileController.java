package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class StaticFileController implements Controller{
    @Override
    public boolean isRunnable(final Http11Request request) {
        return request.isForStaticFile() || request.isDefaultUrl();
    }

    @Override
    public void run(final Http11Request request, final Http11Response response) throws IOException, URISyntaxException {
        response.write(HttpStatus.OK, request.getRequestURL());
    }
}
