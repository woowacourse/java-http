package org.apache.coyote.http11.request.bodyParser;

import java.util.Map;

public interface BodyParser {

    boolean supports(String contentType);

    Map<String, String> parse(String body);
}
