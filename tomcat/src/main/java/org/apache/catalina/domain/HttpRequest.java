package org.apache.catalina.domain;

import java.util.Map;

public record HttpRequest(RequestStartLine requestStartLine, Map<String, String> queryStrings, Map<String, String> headers) {

}
