package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;

public class HttpRequestBody {

    private static final String FORM_DATA_ENTRY_SEPARATOR = "&";
    private static final String FORM_DATA_PARAM_SEPARATOR = "=";

    private final Map<String, String> httpRequestBody;

    private HttpRequestBody(Map<String, String> httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequestBody toHttpRequestBody(String httpRequestBody, ContentType contentType) {
        if (contentType.isFormUrlEncoded()) {
            Map<String, String> requestBody = toRequestBodyMapFromFormUrlEncoded(httpRequestBody);
            return new HttpRequestBody(requestBody);
        }
        throw new IllegalStateException("서버에서 처리되지않은 contentType입니다.");
    }

    private static Map<String, String> toRequestBodyMapFromFormUrlEncoded(String httpRequestBody) {
        Map<String, String> requestBody = new HashMap<>();
        String[] splitFormDatas = httpRequestBody.split(FORM_DATA_ENTRY_SEPARATOR);

        for (String splitFormData : splitFormDatas) {
            validateFormDataForm(splitFormData);
            String[] formDataEntry = splitFormData.split(FORM_DATA_PARAM_SEPARATOR);
            requestBody.put(formDataEntry[0], formDataEntry[1]);
        }
        return requestBody;
    }

    private static void validateFormDataForm(String formDataEntry) {
        if (!formDataEntry.contains("=") || formDataEntry.split(FORM_DATA_PARAM_SEPARATOR).length != 2) {
            throw new IllegalArgumentException("잘못된 FormUrlEncoded 양식입니다.");
        }
    }

    public Map<String, String> getMap() {
        return httpRequestBody;
    }
}
