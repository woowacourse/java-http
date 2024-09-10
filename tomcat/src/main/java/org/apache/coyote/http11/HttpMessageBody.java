package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

public class HttpMessageBody {

    private final String body;

    public HttpMessageBody(String body) {
        this.body = body;
    }

    public static HttpMessageBody createEmptyBody() {
        return new HttpMessageBody("");
    }

    public byte[] getBytes() {
        return body.getBytes();
    }

    public String resolveBodyMessage() {
        return body;
    }

    public String getFormData(String name) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length != 2) {
                throw new UncheckedServletException("요청의 form-data 형식이 올바르지 않습니다");
            }
            if (keyValue[0].equals(name)) {
                return keyValue[1];
            }
        }
        throw new UncheckedServletException(name + "에 해당하는 form-data를 찾지 못했습니다");
    }
}
