package org.apache.coyote.dto;

import java.util.Map;

public record RequestInfo(String method, String path, Map<String, String> queryParams) {
}
