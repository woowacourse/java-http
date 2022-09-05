package org.apache.coyote.http11.http11handler;

import java.util.Map;
import org.slf4j.Logger;

public interface Http11Handler {

    boolean isProperHandler(String uri);

    Map<String, String> handle(Logger log, String uri);
}
