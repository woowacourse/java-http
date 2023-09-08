package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HandlerTestSupport {

    protected HttpRequest makeHttpRequest(final String inputText) {
        try {
            final InputStream inputStream = new ByteArrayInputStream(inputText.getBytes());
            return HttpRequest.from(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("테스트 inputText 가 잘못되었습니다.");
        }
    }

    protected String bytesToText(final byte[] inputBytes) {
        return new String(inputBytes, StandardCharsets.UTF_8);
    }
}
