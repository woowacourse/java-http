package org.apache.coyote.httpRequest.httpBody;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.httpRequest.httpHeader.ContentType;

public class HttpBody {

    private final String body;
    private final Map<String, String> bodyData;

    public HttpBody(
            final String body,
            final ContentType contentType
    ) {
        this.body = body;
        this.bodyData = createBodyData(body, contentType);
    }

    public String getData(String key) {
        return bodyData.getOrDefault(key, null);
    }

    private Map<String, String> createBodyData(
            final String body,
            final ContentType contentType
    ) {
        return switch (contentType) {
            case APPLICATION_FORM_URLENCODED -> createBodyDataByForm(body);
            default -> throw new IllegalArgumentException("지원하지 않는 Content-Type: " + contentType);
        };
    }

    private Map<String, String> createBodyDataByForm(final String body) {
        final Map<String, String> bodyData = new HashMap<>();
        final String[] datas = body.split("&");
        for (String data : datas) {
            String[] dataResult = data.split("=");
            bodyData.put(dataResult[0], dataResult[1]);
        }
        return bodyData;
    }

}
