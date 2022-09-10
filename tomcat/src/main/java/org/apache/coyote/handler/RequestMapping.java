package org.apache.coyote.handler;

public interface RequestMapping {

    Controller find(final String requestUri);
}
