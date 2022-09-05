package org.apache.coyote.http11.http11handler;

import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11request.Http11Request;
import org.slf4j.Logger;

public interface Http11Handler {

    boolean isProperHandler(Http11Request http11Request);

    ResponseComponent handle(Logger log, String uri);
}
