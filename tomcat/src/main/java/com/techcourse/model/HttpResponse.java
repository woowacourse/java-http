package com.techcourse.model;

import java.util.Map;

public record HttpResponse(
        String statusCode,
        Map<String, String> headers,
        byte[] body

) {

}
