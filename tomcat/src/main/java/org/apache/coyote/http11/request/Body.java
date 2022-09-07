package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;

public class Body {

    private final String value;

    public Body(String value) {
        this.value = value;
    }

    public Map<String, String> toApplicationForm() {
        HashMap<String, String> applicationForm = new HashMap<>();
        for (String form : value.split("&")) {
            String[] keyValue = form.split("=");
            applicationForm.put(keyValue[0], keyValue[1]);
        }
        return applicationForm;
    }
}
