package nextstep.jwp.controller;

import nextstep.jwp.exception.DuplicateUserException;
import nextstep.jwp.exception.FileNotFoundException;
import nextstep.jwp.exception.InvalidSignUpFormatException;
import nextstep.jwp.exception.QueryStringFormatException;
import nextstep.jwp.exception.UserLoginException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.java.servlet.ExceptionHandler;

public class AppExceptionHandler implements ExceptionHandler {

    private static final String BAD_REQUEST_ERROR_PAGE_URL = "./400.html";
    private static final String UNAUTHORIZED_ERROR_PAGE_URL = "./401.html";
    private static final String NOT_FOUND_ERROR_PAGE_URL = "./404.html";
    private static final String INTERNAL_SERVER_ERROR_PAGE_URL = "./500.html";

    @Override
    public void handle(final Exception exception, final HttpResponse response) {
        try {
            throw exception;
        } catch (FileNotFoundException | UserNotFoundException e) {
            response.sendRedirect(NOT_FOUND_ERROR_PAGE_URL);
        } catch (UserLoginException e) {
            response.sendRedirect(UNAUTHORIZED_ERROR_PAGE_URL);
        } catch (InvalidSignUpFormatException | DuplicateUserException | QueryStringFormatException e) {
            response.sendRedirect(BAD_REQUEST_ERROR_PAGE_URL);
        } catch (Exception e) {
            response.sendRedirect(INTERNAL_SERVER_ERROR_PAGE_URL);
        }
    }
}
