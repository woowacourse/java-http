package org.apache.catalina.servlet;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final UnsupportedOperationException METHOD_NOT_ALLOWED = new UnsupportedOperationException("요청하신 HTTP METHOD는 지원하지 않습니다.");

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getHttpMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw METHOD_NOT_ALLOWED;
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw METHOD_NOT_ALLOWED;
    }
}
