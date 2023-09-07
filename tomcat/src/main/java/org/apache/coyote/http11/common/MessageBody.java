package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageBody {

    private final String value;

    private MessageBody(final String value) {
        this.value = value;
    }

    public static MessageBody from(final String messageBody) {
        return new MessageBody(messageBody);
    }

    public static MessageBody empty() {
        return new MessageBody("");
    }

    public int getBodyLength() {
        return value.getBytes().length;
    }

    public String getValue() {
        return value;
    }

    public Map<String, Object> getFormData() {
        Map<String, Object> formDataMap = new LinkedHashMap<>();

        String[] formData = value.split("&");
        for (String data : formData) {
            String[] dataPair = data.split("=");
            formDataMap.put(dataPair[0], dataPair[1]);
        }
        return formDataMap;
    }
}
