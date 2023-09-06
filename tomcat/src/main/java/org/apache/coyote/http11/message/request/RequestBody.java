package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class RequestBody {

    private final String content;

    public RequestBody(String content) {
        this.content = content;
    }

    public static RequestBody ofEmpty() {
        return new RequestBody(StringUtils.EMPTY);
    }

    public Map<String, String> getAsFormData() {
        return Arrays.stream(content.split("&"))
                .map(each -> each.split("="))
                .map(each -> Map.entry(each[0], each[1]))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
