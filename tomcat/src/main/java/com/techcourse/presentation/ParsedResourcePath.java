package com.techcourse.presentation;

import java.util.Map;

public record ParsedResourcePath(
        String path,
        Map<String, String> params
) {

}
