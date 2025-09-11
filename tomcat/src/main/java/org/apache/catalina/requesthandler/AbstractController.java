package org.apache.catalina.requesthandler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request, HttpResponse response) throws Exception {
        Method method = request.getMethod();
        if(method == Method.GET) {
            doGet(request, response);
            return response;
        }
        if(method == Method.POST) {
            doPost(request, response);
            return response;
        }
        if(method == Method.PUT) {
            doPut(request, response);
            return response;
        }
        if(method == Method.DELETE) {
            doDelete(request, response);
            return response;
        }
        if(method == Method.PATCH) {
            doPatch(request, response);
            return response;
        }
        if(method == Method.HEAD) {
            doHead(request, response);
            return response;
        }
        if(method == Method.OPTIONS) {
            doOptions(request, response);
            return response;
        }
        if(method == Method.TRACE) {
            doTrace(request, response);
            return response;
        }
        throw new UnsupportedOperationException("지원하지 않는 method 요청입니다.: " + request.getMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doPut(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doDelete(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doPatch(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doHead(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doOptions(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doTrace(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
