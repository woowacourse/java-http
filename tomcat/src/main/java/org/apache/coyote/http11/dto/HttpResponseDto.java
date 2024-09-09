package org.apache.coyote.http11.dto;

import java.util.Map;
import org.apache.coyote.http11.domain.response.HttpResponse;

public record HttpResponseDto(
        int httpStatusCode,
        String reasonPhrase,
        Map<String, String> responseHeaders,
        String messageBody
) {
    public static HttpResponseDto from(HttpResponse httpResponse) {
        return new HttpResponseDto(
                httpResponse.getHttpStatus().getCode(),
                httpResponse.getHttpStatus().getReasonPhrase(),
                httpResponse.getResponseHeaders(),
                httpResponse.getMessageBody()
        );
    }
}
