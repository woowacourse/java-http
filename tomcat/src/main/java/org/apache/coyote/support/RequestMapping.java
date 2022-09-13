package org.apache.coyote.support;

import org.apache.coyote.http11.request.Http11Request;

public interface RequestMapping {

    Controller getController(Http11Request http11Request);
}
