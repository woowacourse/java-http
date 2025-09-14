package com.techcourse.web.view;

import common.HttpStatus;
import java.util.Map;

public sealed interface AppResponse
        permits StandardResponse {

    HttpStatus status();

    Map<String, String> headers();

    byte[] body();
}
