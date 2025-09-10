package org.apache.catalina;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class HttpServlet implements Servlet {

    @Override
    public abstract void init();

    @Override
    public final void service(final HttpRequest request, final HttpResponse response) {
        if (HttpMethod.GET == request.getMethod()) {
            doGet(request, response);
            return;
        }

        if (HttpMethod.POST == request.getMethod()) {
            doPost(request, response);
            return;
        }

        if (HttpMethod.PUT == request.getMethod()) {
            doPut(request, response);
            return;
        }

        if (HttpMethod.DELETE == request.getMethod()) {
            doDelete(request, response);
            return;
        }

        // 405 Method Not Allowed 기본 처리
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.write("<html><body><h1>405 Method Not Allowed</h1></body></html>");
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
        sendMethodNotAllowed(response, "GET");
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
        sendMethodNotAllowed(response, "POST");
    }

    protected void doPut(final HttpRequest request, final HttpResponse response) {
        sendMethodNotAllowed(response, "PUT");
    }

    protected void doDelete(final HttpRequest request, final HttpResponse response) {
        sendMethodNotAllowed(response, "DELETE");
    }

    private void sendMethodNotAllowed(final HttpResponse response, final String method) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.write("<html><body><h1>HTTP method " + method + " is not supported by this URL</h1></body></html>");
    }

    protected String createErrorPage(final String message) {
        return String.format("""
                <html>
                <head><title>Error %d</title></head>
                <body>
                    <h1>%s</h1>
                    <p>Status Code: %d</p>
                </body>
                </html>
                """, 500, message, 500);
    }

    @Override
    public abstract void destroy();
}
