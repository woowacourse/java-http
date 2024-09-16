package jakarta.controller;

import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
