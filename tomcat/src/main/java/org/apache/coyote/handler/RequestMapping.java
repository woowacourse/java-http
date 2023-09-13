package org.apache.coyote.handler;

public interface RequestMapping {

    Controller getHandler(final String requestUri);
}
