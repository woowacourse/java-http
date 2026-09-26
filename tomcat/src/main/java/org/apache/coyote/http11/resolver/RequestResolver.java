package org.apache.coyote.http11.resolver;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public interface RequestResolver {
    Response handleRequest(Request request);
    boolean canHandle(Request request);
}
