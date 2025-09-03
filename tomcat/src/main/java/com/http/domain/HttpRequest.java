package com.http.domain;

import java.util.Map;

public record HttpRequest(StartLine startLine, Map<String, String> queryStrings, Map<String, String> headers) {

}
