package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class RequestHandler {

    public static Response handle(Request request) throws IOException, URISyntaxException {
        final Controller controller = RequestMapper.getController(request);
        try {
            return controller.service(request);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static Response handleException(final Exception e) throws IOException, URISyntaxException {
        if (e instanceof NotFoundException) {
            return new Response(StatusCode.NOT_FOUND, ResourceLoader.getStaticResource("/404.html"));
        }

        if (e instanceof IllegalArgumentException) {
            return new Response(StatusCode.BAD_REQUEST, "잘못된 요청입니다.");
        }

        if (e instanceof UnauthorizedException) {
            return new Response(StatusCode.UNAUTHORIZED, ResourceLoader.getStaticResource("/401.html"));
        }

        return new Response(StatusCode.INTERNAL_SERVER_ERROR, ResourceLoader.getStaticResource("/500.html"));
    }
}
