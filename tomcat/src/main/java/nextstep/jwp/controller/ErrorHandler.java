package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.exception.WrongInputException;
import org.apache.coyote.ExceptionHandler;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class ErrorHandler implements ExceptionHandler {

    private static final ErrorHandler INSTANCE = new ErrorHandler();

    public static ErrorHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public HttpResponse handle(final Exception e) {
        try {
            return mapResponse(e);
        } catch (IOException | URISyntaxException ex) {
            return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, "페이지를 읽지 못했습니다.");
        }
    }

    private static HttpResponse mapResponse(final Exception e) throws IOException, URISyntaxException {
        if (e instanceof NotFoundException) {
            return new HttpResponse(StatusCode.NOT_FOUND, View.NOT_FOUND.getResource());
        }

        if (e instanceof WrongInputException) {
            return new HttpResponse(StatusCode.BAD_REQUEST, "잘못된 요청입니다: " + e.getMessage());
        }

        if (e instanceof UnauthorizedException) {
            return HttpResponse.ofRedirection(StatusCode.SEE_OTHER, View.UNAUTHORIZED.getResource());
        }

        if (e instanceof UnsupportedMethodException) {
            return new HttpResponse(StatusCode.METHOD_NOT_ALLOWED, "처리할 수 없는 요청입니다.");
        }

        return new HttpResponse(StatusCode.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR.getResource());
    }

    private ErrorHandler() {
    }
}
