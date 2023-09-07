package org.apache.coyote.adapter;

import org.apache.coyote.request.Request;
import org.apache.coyote.view.Resource;

public interface Adapter {

    Resource adapt(Request request);
}
