package com.techcourse.model;

import java.util.List;
import java.util.Map;

public record HttpResponse(
        String statusCode,
        Map<String, List<String>> headers,
        byte[] body

) {

}
