package org.apache.catalina.servlet;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public abstract class HttpServlet implements Servlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            doGet(request, response);
            return;
        }

        if (method == HttpMethod.POST) {
            doPost(request, response);
            return;
        }

        response.setStatus(HttpStatus.NOT_IMPLEMENTED);
        response.setContentType(ContentType.PLAIN);
        response.appendToBody("501 Not Implemented".getBytes());
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        send405Error(response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        send405Error(response);
    }

    private void send405Error(HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setContentType(ContentType.PLAIN);
        response.appendToBody("405 Method Not Allowed".getBytes());
    }
}
