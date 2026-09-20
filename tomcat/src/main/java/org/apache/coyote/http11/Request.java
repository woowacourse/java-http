package org.apache.coyote.http11;

import java.util.Map;

public record Request(String path, Map<String, String> params) {
}