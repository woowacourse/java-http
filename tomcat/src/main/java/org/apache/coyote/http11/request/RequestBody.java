package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestBody {
    private final Map<String, String> contents;

    public RequestBody(final Map<String, String> contents) {
        this.contents = contents;
    }

    public static RequestBody from(final String input) {
        Map<String, String> contents = new LinkedHashMap<>();
        Arrays.stream(input.split("&"))
                .map(part -> part.split("="))
                .forEach(parts -> contents.put(parts[0], parts[1]));
        return new RequestBody(contents);
    }

    public String getContentValue(final String key) {
        if(!contents.containsKey(key)) {
            return null;
        }
        return contents.get(key);
    }
}
