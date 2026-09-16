package org.qupring.mvc.handler;

import org.apache.http.HttpMethod;

public record MappingTarget(
        String path,
        HttpMethod method
) {
}
