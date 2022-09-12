package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final String value;

    private RequestBody(String value) {
        this.value = value;
    }

    public static RequestBody from(String value) {
        return new RequestBody(value);
    }

    public Map<String, String> toApplicationForm() {
        Map<String, String> applicationForm = new HashMap<>();
        for (String form : value.split("&")) {
            String[] keyValue = form.split("=");
            applicationForm.put(keyValue[0], keyValue[1]);
        }
        return applicationForm;
    }
}
