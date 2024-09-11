package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private static final String JSON_LINE_SEPARATOR = System.lineSeparator();

    private static final String JSON_PARAM_SEPARATOR = ":";
    private static final String FORM_DATA_ENTRY_SEPARATOR = "&";
    private static final String FORM_DATA_PARAM_SEPARATOR = "=";

    private final Map<String, String> httpRequestBody;

    private HttpRequestBody(Map<String, String> httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequestBody toHttpRequestBody(
            String httpRequestBody,
            ContentType contentType
    ) {
        if (contentType.isJson()) {
            Map<String, String> requestBody = toRequestBodyMapFromJson(httpRequestBody);
            return new HttpRequestBody(requestBody);
        }
        if (contentType.isFromData()) {
            Map<String, String> requestBody = toRequestBodyMapFromFormData(httpRequestBody);
            return new HttpRequestBody(requestBody);
        }
        throw new IllegalStateException("서버에서 처리되지않은 contentType입니다.");
    }

    private static Map<String, String> toRequestBodyMapFromJson(String httpRequestBody) {
        Map<String, String> requestBody = new HashMap<>();
        String[] requestBodyLines = httpRequestBody.split(JSON_LINE_SEPARATOR);
        for (int i = 0; i <= requestBody.size(); i++) {
            String[] splitBody = requestBodyLines[i].split(JSON_PARAM_SEPARATOR);
            validateJsonForm(splitBody);
            requestBody.put(splitBody[0], splitBody[1]);
        }
        return requestBody;
    }

    private static Map<String, String> toRequestBodyMapFromFormData(String httpRequestBody) {
        Map<String, String> requestBody = new HashMap<>();
        String[] splitFormData = httpRequestBody.split(FORM_DATA_ENTRY_SEPARATOR);
        for (String splitFormDatum : splitFormData) {
            String[] formDataEntry = splitFormDatum.split(FORM_DATA_PARAM_SEPARATOR);
            validateFormDataForm(formDataEntry);
            requestBody.put(formDataEntry[0], formDataEntry[1]);
        }
        return requestBody;
    }

    private static void validateFormDataForm(String[] formDataEntry) {
        if (formDataEntry.length != 2) {
            throw new IllegalArgumentException("잘못된 HTTP/1.1 FormData 양식입니다.");
        }
    }

    private static void validateJsonForm(String[] splitBody) {
        if (splitBody.length != 2) {
            throw new IllegalArgumentException("잘못된 HTTP/1.1 JSON 양식입니다.");
        }
    }

    public Map<String, String> getMap() {
        return httpRequestBody;
    }
}
