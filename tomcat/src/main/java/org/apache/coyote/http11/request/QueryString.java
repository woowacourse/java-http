package org.apache.coyote.http11.request;

import java.util.Map;

public record QueryString(Map<String, String> queryStrings) {
}
