package com.techcourse.presentation;

import java.util.Map;

public record HttpResponse(
        String protocol,
        String statusCode,
        Map<String, String> headers,
        String body
) {

}
