package nextstep.java.servlet;

import nextstep.jwp.http.response.HttpResponse;

public interface ExceptionHandler {

    void handle(Exception exception, HttpResponse response);
}
