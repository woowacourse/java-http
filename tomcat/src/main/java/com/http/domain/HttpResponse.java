package com.http.domain;

import java.util.Map;

public record HttpResponse(String startLine, Map<String, String> headers, byte[] body) {

}
