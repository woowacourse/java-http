package org.apache.coyote.adapter;

import org.apache.coyote.handler.DefaultHandler;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.view.Resource;

public class StringAdapter implements Adapter {

    @Override
    public Resource doHandle(Request request) {
        DefaultHandler defaultHandler = new DefaultHandler();
        String resource = defaultHandler.response();

        return new Resource(resource, HttpStatus.OK);
    }
}
