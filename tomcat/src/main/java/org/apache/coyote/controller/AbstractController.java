package org.apache.coyote.controller;

import org.apache.coyote.request.Method;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractController implements Controller {

    @Override
    public void service(MyHttpRequest request, MyHttpResponse response) throws Exception {
        List<Method> allowedMethods = allowedMethods();
        if (!allowedMethods.contains(request.method())) {
            response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED);
            response.addHeader(
                    "Allow",
                    allowedMethods.stream()
                            .map(Method::name)
                            .collect(Collectors.joining(", "))
            );
            return;
        }

        if (request.isGet()) {
            doGet(request, response);
            return;
        }

        if (request.isPost()) {
            doPost(request, response);
            return;
        }
    }

    protected abstract List<Method> allowedMethods();

    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws Exception {
    }

    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
    }
}
