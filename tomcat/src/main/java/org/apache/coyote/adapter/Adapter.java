package org.apache.coyote.adapter;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface Adapter {

    Response doHandle(Request request);
}
