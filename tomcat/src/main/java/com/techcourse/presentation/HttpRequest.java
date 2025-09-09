package com.techcourse.presentation;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        String protocol,
        Map<String, String> params,
        Map<String, String> headers,
        String body
) {

}
