package org.apache.catalina.connector;

import org.apache.catalina.core.CatalinaContainer;
import org.apache.catalina.core.ContextConfig;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class CoyoteAdapter implements Adapter {

    private final CatalinaContainer catalinaContainer;

    public CoyoteAdapter() {
        this.catalinaContainer = new CatalinaContainer(
                ContextConfig.REQUEST_HANDLERS,
                ContextConfig.EXCEPTION_HANDLER
        );
    }

    @Override
    public void service(final Http11Request request, final Http11Response response) {
        catalinaContainer.handle(request, response);
    }
}
