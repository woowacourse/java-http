package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ExceptionController {
    Logger log = LoggerFactory.getLogger(Application.class);

    boolean isResolvable(Exception exception);
    void handle(Exception exception, Response response) throws IOException, URISyntaxException;
}
