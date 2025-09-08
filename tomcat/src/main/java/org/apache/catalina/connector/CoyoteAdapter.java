package org.apache.catalina.connector;

import org.apache.catalina.context.ApplicationContext;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class CoyoteAdapter implements Adapter {

    private final HandlerDispatcher handlerDispatcher;

    public CoyoteAdapter() {
        this.handlerDispatcher = new HandlerDispatcher(
                ApplicationContext.REQUEST_HANDLERS,
                ApplicationContext.EXCEPTION_HANDLER
        );
    }

    @Override
    public void service(final Http11Request request, final Http11Response response) {
        handlerDispatcher.handle(request, response);
    }
}
