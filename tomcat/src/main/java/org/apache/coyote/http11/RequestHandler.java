package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static HttpResponse handle(HttpRequest httpRequest) throws IOException, URISyntaxException {
        final Controller controller = RequestMapper.getController(httpRequest);
        try {
            return controller.service(httpRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return handleException(e);
        }
    }

    private static HttpResponse handleException(final Exception e) throws IOException, URISyntaxException {
        if (e instanceof NotFoundException) {
            return new HttpResponse(StatusCode.NOT_FOUND, ResourceLoader.getStaticResource("/404.html"));
        }

        if (e instanceof IllegalArgumentException) {
            return new HttpResponse(StatusCode.BAD_REQUEST, "잘못된 요청입니다: " + e.getMessage());
        }

        if (e instanceof UnauthorizedException) {
            return HttpResponse.ofRedirection(StatusCode.SEE_OTHER, "/401.html");
        }

        if (e instanceof UnsupportedMethodException) {
            return new HttpResponse(StatusCode.METHOD_NOT_ALLOWED, "처리할 수 없는 요청입니다.");
        }

        return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, ResourceLoader.getStaticResource("/500.html"));
    }
}
