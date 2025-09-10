package org.apache.coyote.httpRequest.httpBody;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;
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
            default -> throw new HttpException(ErrorCode.NOT_ALLOW_MEDIA_TYPE);
        };
    }

    private Map<String, String> createBodyDataByForm(final String body) {
        final Map<String, String> bodyData = new HashMap<>();
        final String[] datas = body.split("&");
        for (String data : datas) {
            final String[] dataResult = data.split("=");
            if (dataResult.length == 2) {
                bodyData.put(dataResult[0], dataResult[1]);
            }
        }
        return bodyData;
    }

}
