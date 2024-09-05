package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse httpResponse) {
    }

    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
    }

    protected void doGet(HttpRequest request, HttpResponse httpResponse) {
    }
}
