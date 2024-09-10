package org.apache.coyote.http11.message;

import java.util.Map;
import org.apache.coyote.http11.domain.response.HttpResponse;

public record HttpResponseMessage(
        int httpStatusCode,
        String reasonPhrase,
        Map<String, String> responseHeaders,
        String messageBody
) {
    public static HttpResponseMessage from(HttpResponse httpResponse) {
        return new HttpResponseMessage(
                httpResponse.getHttpStatus().getCode(),
                httpResponse.getHttpStatus().getReasonPhrase(),
                httpResponse.getResponseHeaders(),
                httpResponse.getMessageBody()
        );
    }
}
