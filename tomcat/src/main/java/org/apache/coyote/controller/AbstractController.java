package org.apache.coyote.controller;

import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(MyHttpRequest request, MyHttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }

        if (request.isPost()) {
            doPost(request, response);
            return;
        }
    }

    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws Exception {
    }

    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
    }
}
