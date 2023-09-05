package org.apache.coyote.response;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseBody {

    private static final String EMPTY_BODY = "";

    private final String value;

    public ResponseBody(final String value) {
        this.value = value;
    }

    public static ResponseBody empty() {
        return new ResponseBody(EMPTY_BODY);
    }

    public byte[] bytes() {
        return value.getBytes(UTF_8);
    }

    public int length() {
        return bytes().length;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "ResponseBody.Length{" +
               "source='" + value.length() + '\'' +
               '}';
    }
}
