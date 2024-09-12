package com.techcourse.servlet.mapper;

import org.apache.coyote.http11.HttpMethod;

public record RequestValue(HttpMethod method, String targetPath) {
}
