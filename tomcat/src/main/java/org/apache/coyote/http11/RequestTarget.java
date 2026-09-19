package org.apache.coyote.http11;

import java.util.Map;

public record RequestTarget(
    String path,
    Map<String, String> queryParams
) {

}
