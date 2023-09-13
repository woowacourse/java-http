package org.apache.coyote.http11;

import java.util.Map;

public interface BodyParser {

    Map<String, Object> parse(final String body);
}
