package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public non-sealed abstract class AbstractController implements Controller {

    @Override
    public final void service(Http11Request request, Http11Response response) {
        switch (getMethod(request)) {
            case GET -> doGet(request, response);
            case PUT -> doPut(request, response);
            case HEAD -> doHead(request, response);
            case POST -> doPost(request, response);
            case TRACE -> doTrace(request, response);
            case DELETE -> doDelete(request, response);
            case CONNECT -> doConnect(request, response);
            case OPTIONS -> doOptions(request, response);
            case null, default -> throw new RuntimeException("지원하지 않는 메서드 입니다.");
        }
    }

    private Http11Method getMethod(Http11Request request) {
        return request.method();
    }

    protected void doGet(Http11Request request, Http11Response response) {

    }

    protected void doPut(Http11Request request, Http11Response response) {

    }

    protected void doHead(Http11Request request, Http11Response response) {

    }

    protected void doPost(Http11Request request, Http11Response response) {

    }

    protected void doTrace(Http11Request request, Http11Response response) {

    }

    protected void doDelete(Http11Request request, Http11Response response) {

    }

    protected void doConnect(Http11Request request, Http11Response response) {

    }

    protected void doOptions(Http11Request request, Http11Response response) {

    }
}
