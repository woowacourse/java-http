package org.apache.front;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public class Proxy {

    private final StaticController staticController;

    private final DynamicController dynamicController;

    public Proxy() {
        this.staticController = new StaticController();
        this.dynamicController = new DynamicController();
    }

    public Response process(Request request){
        if(request.isFile()){
            return doProcess(staticController, request);
        }
        return doProcess(dynamicController, request);
    }

    private Response doProcess(FrontController frontController, Request request) {
        return frontController.process(request);
    }

}
