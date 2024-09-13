package com.techcourse.resolver;


import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

@Method
@Location
public class HomeResolver extends HttpRequestResolver {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String extension = "html";
        String body = "hello world!";

        response.setBody(body);
    }

    @Override
    public HttpResponse resolve(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
        return response;
    }
}
