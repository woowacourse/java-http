package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;

public interface RequestHandler {
    Response doGet(Request request);
    Response doPost(Request request);
    boolean canHandle(Request request);
}
