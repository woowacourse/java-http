package org.apache.coyote.http11;

import java.util.Map;

public record HttpResponse(String statusCode, Map<String, String> headers, byte[] body) {
}
