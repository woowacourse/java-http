package org.apache.coyote.http11.http11handler;

import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11request.Http11Request;

public interface Http11Handler {

    boolean isProperHandler(Http11Request http11Request);

    ResponseComponent handle(Http11Request http11Request);
}
