package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {

        if (GET.equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if (POST.equals(request.getMethod())) {
            doPost(request, response);
        }
    }

    //하위 클래스가 필요한 메서드만 재정의한다.
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }
}