package org.apache.coyote.http11.request.body.parser;

import java.util.Map;

@FunctionalInterface
public interface BodyParser {

    Map<String, String> parse(String body);
}
