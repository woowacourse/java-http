package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.requestLine().method();
        if ("GET".equals(method)) {
            doGet(request, response);
            return;
        }
        if ("POST".equals(method)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException("지원하지 않는 HTTP 메서드: " + method);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
