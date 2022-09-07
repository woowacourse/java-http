package nextstep.jwp;

import static org.apache.coyote.http11.StatusCode.OK;

import java.util.function.Function;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHandler implements Function<Http11Request, Http11Response> {

    private static final Logger log = LoggerFactory.getLogger(ResourceHandler.class);

    @Override
    public Http11Response apply(Http11Request request) {
        return Http11Response.of(OK, request.getRequestUrl());
    }
}
