package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse){
        if(httpRequest.isGet()){
            doGet(httpRequest, httpResponse);
        }

        if(httpRequest.isPost()){
            doPost(httpRequest, httpResponse);
        }
    };

    abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

    abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

}
