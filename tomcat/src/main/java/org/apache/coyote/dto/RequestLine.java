package org.apache.coyote.dto;

import java.util.Map;

public record RequestLine(String method, String path, Map<String, String> queryParams, String version) {
}
