package org.apache.coyote.http11.request.bodyparser;

import java.util.Map;

@FunctionalInterface
public interface RequestBodyParser {

    Map<String, String> parseParameters(String requestBody);
}
