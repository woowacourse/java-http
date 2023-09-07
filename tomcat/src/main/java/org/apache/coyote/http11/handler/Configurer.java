package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.Request;

public class Configurer {
    private static final Handler loginHandler;
    private static final Handler staticHandler;
    private static final Handler apiHandler;
    private static final Handler failedHandler;

    static{
        loginHandler = new LoginHandler();
        staticHandler = new StaticHandler();
        apiHandler = new ApiHandler();
        failedHandler = new FailedHandler();

        loginHandler.setNext(staticHandler);
        staticHandler.setNext(apiHandler);
        apiHandler.setNext(failedHandler);
    }

    public static String handle(Request request){
        return loginHandler.getResponse(request);
    }
}
