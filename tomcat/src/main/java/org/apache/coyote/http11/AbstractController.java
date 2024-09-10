package org.apache.coyote.http11;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String UNSUPPORTED_METHOD_MESSAGE = "지원하지 않는 HTTP Method 입니다.";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case "GET":
                doGet(request, response);
                break;
            case "POST":
                doPost(request, response);
                break;
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException(UNSUPPORTED_METHOD_MESSAGE);
    }
}
