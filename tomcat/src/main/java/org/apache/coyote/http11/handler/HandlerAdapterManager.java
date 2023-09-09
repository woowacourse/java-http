package org.apache.coyote.http11.handler;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ViewController;

public class HandlerAdapterManager {
    private final HandlerAdapter handlerAdapter;

    public HandlerAdapterManager() {
        this.handlerAdapter = new HandlerAdapter();
        add();
    }

    private void add(){
        handlerAdapter.addRequestFunctions(HttpMethod.POST,"/login",LoginController::login);
        handlerAdapter.addRequestFunctions(HttpMethod.POST,"/register",LoginController::signUp);
        handlerAdapter.addNonRequestController(HttpMethod.GET,"/login", ViewController::getLogin);
        handlerAdapter.addNonRequestController(HttpMethod.GET,"/register",ViewController::getRegister);
        handlerAdapter.addNonRequestController(HttpMethod.GET,"/",ViewController::getVoid);
    }

    public HandlerAdapter getHandlerAdapter() {
        return handlerAdapter;
    }
}
