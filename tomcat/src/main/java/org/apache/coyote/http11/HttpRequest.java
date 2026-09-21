package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequest(HttpMethod httpMethod, String path, Map<String, String> params) {
}
