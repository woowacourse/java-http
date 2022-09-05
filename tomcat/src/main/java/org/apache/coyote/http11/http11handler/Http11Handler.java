package org.apache.coyote.http11.http11handler;

import org.apache.coyote.http11.dto.ResponseComponent;
import org.slf4j.Logger;

public interface Http11Handler {

    boolean isProperHandler(String uri);

    ResponseComponent handle(Logger log, String uri);
}
