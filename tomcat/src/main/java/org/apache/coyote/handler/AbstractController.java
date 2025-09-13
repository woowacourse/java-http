package org.apache.coyote.handler;

import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;
import org.apache.coyote.render.MethodType;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse response){
        if (httpRequest.method().equals(MethodType.GET.getMethod())) {
            doGet(httpRequest,response);
        }
        if (httpRequest.method().equals(MethodType.POST.getMethod())) {
            doPost(httpRequest,response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) { }
    protected void doGet(HttpRequest request, HttpResponse response)  { }
}