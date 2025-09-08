package org.apache.catalina.servlet;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

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

        //TODO: 다른 HttpMethod 분기 작성  (2025-09-7, 일, 23:59)
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
