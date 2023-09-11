package org.apache.coyote.http11.message.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestForms {

    private static final String FORM_DELIMITER = "&";
    private static final String FORM_PAIR_DELIMITER = "=";
    private static final int HEADER_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> formData;

    public RequestForms(final Map<String, String> formData) {
        this.formData = formData;
    }

    public static RequestForms from(final String values) {
        final Map<String, String> requestForms = new LinkedHashMap<>();

        final String[] splitedValues = values.split(FORM_DELIMITER);
        for (final String value : splitedValues) {
            final String[] formPair = value.split(FORM_PAIR_DELIMITER);
            requestForms.put(formPair[HEADER_INDEX], formPair[VALUE_INDEX]);
        }
        return new RequestForms(requestForms);
    }

    public Map<String, String> getFormData() {
        return formData;
    }
}
