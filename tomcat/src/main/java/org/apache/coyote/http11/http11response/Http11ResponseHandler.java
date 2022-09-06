package org.apache.coyote.http11.http11response;

import org.apache.coyote.http11.dto.ResponseComponent;

public interface Http11ResponseHandler {

    boolean isProper(ResponseComponent responseComponent);

    String makeResponse(ResponseComponent responseComponent);
}
