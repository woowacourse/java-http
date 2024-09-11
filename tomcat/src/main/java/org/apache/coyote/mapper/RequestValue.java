package org.apache.coyote.mapper;

import org.apache.coyote.http11.HttpMethod;

public record RequestValue(HttpMethod method, String targetPath) {
}
