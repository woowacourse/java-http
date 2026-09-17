package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record RequestTarget(
        String path,
        List<String> query
) {
    public static RequestTarget from(String requestTarget) {
        String path = requestTarget.split("\\?")[0].split("#")[0];
        if (!requestTarget.contains("?")) {
            return new RequestTarget(path, Collections.emptyList());
        }

        String[] queries = requestTarget.split("\\?")[1].split("#")[0].split("&");
        return new RequestTarget(path, Arrays.asList(queries));
    }
}
