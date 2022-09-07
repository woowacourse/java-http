package nextstep.jwp.ui;

import static org.apache.coyote.http11.response.StatusCode.OK;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.support.AbstractController;

public final class ResourceController extends AbstractController {

    @Override
    protected Http11Response doGet(final Http11Request request) {
        return Http11Response.of(OK, request.getRequestUrl());
    }
}
