package org.apache.coyote.http11.request.parser;

import java.util.Map;

public interface PayloadParser {

    Map<String, String> parse(final String body);
}
