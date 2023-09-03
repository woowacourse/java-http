package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    final private Map<String, String> contents;

    public RequestBody(final Map<String, String> contents) {
        this.contents = contents;
    }

    public static RequestBody from(final String input) {
        Map<String, String> contents = new HashMap<>();
        String[] body = input.split("&");
        for(String part : body) {
            String[] componentOfPart = part.split("=");
            contents.put(componentOfPart[0], componentOfPart[1]);
        }
        return new RequestBody(contents);
    }

    public String getContentValue(final String header) {
        if(!contents.containsKey(header)) {
            return null;
        }
        return contents.get(header);
    }
}
