package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.exception.PageRedirectException;

public class Proxy {

    private final StaticController staticController;

    private final DynamicController dynamicController;

    public Proxy() {
        this.staticController = StaticController.singleTone();
        this.dynamicController = DynamicController.singleTone();
    }

    public ResponseEntity process(final Request request){
        if(request.isStatic()){
            return doProcess(staticController, request);
        }
        return doProcess(dynamicController, request);
    }

    private ResponseEntity doProcess(final FrontController frontController, final Request request) {
        try{
            return frontController.process(request);
        } catch (PageRedirectException pageRedirectException){
            return pageRedirectException.getResponseEntity();
        }
    }

}
