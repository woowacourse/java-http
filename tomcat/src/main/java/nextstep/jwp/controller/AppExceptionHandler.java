package nextstep.jwp.controller;

import nextstep.jwp.http.response.HttpResponse;
import org.apache.catalina.core.controller.ExceptionHandler;

public class AppExceptionHandler implements ExceptionHandler {

    private static final String UNAUTHORIZED_ERROR_PAGE_URL = "./401.html";

    @Override
    public void handle(Exception exception, HttpResponse response) {

    }
}
