package org.apache.coyote.controller;

import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

public interface Handler {
    Response handle(Request request);
}
