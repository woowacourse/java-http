package nextstep.jwp.presentation;

import http.HttpHeaders;
import http.HttpResponse;
import http.HttpStatus;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.InvalidSignUpFormatException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.catalina.exception.InvalidHttpMethodException;
import org.apache.catalina.exception.InvalidHttpRequestFormatException;
import org.apache.catalina.exception.QueryStringFormatException;
import org.apache.catalina.exception.ResourceNotFoundException;
import org.apache.catalina.handler.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler implements ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String BAD_REQUEST_PATH = "/400.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String METHOD_NOT_ALLOWED_PATH = "/405.html";
    private static final String INTERNAL_SERVER_ERROR_PATH = "/500.html";

    private GlobalExceptionHandler() {
    }

    public static GlobalExceptionHandler instance() {
        return GlobalExceptionHandlerHolder.instance;
    }

    @Override
    public HttpResponse handle(final Exception e) {
        try {
            throw e;
        } catch (InvalidLoginFormatException | InvalidPasswordException | MemberNotFoundException ignored) {
            log.error(e.getMessage(), e);
            return redirect(UNAUTHORIZED_PATH);
        } catch (ResourceNotFoundException ignored) {
            log.error(e.getMessage(), e);
            return redirect(NOT_FOUND_PATH);
        } catch (InvalidHttpRequestFormatException | QueryStringFormatException | InvalidSignUpFormatException |
                 DuplicateAccountException | IllegalArgumentException ignored) {
            log.error(e.getMessage(), e);
            return redirect(BAD_REQUEST_PATH);
        } catch (InvalidHttpMethodException | MethodNotAllowedException ignored) {
            return redirect(METHOD_NOT_ALLOWED_PATH);
        } catch (Exception ignored) {
            log.error(e.getMessage(), e);
            return redirect(INTERNAL_SERVER_ERROR_PATH);
        }
    }

    private HttpResponse redirect(final String location) {
        HttpHeaders responseHeaders = HttpHeaders.createEmpty();
        responseHeaders.add("Location", location);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    private static class GlobalExceptionHandlerHolder {

        private static final GlobalExceptionHandler instance = new GlobalExceptionHandler();
    }
}
