package org.apache.coyote.http11;

import java.util.Map;

public interface BodyParser {

    Map<String, String> parse(final String body);
}
