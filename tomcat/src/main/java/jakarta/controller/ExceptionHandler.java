package jakarta.controller;

import jakarta.http.response.HttpResponse;

public interface ExceptionHandler {

    void handle(Exception exception, HttpResponse response);
}
