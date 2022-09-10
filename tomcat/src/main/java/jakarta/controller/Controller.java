package jakarta.controller;


import jakarta.http.reqeust.HttpRequest;
import jakarta.http.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
