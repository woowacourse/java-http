package org.apache.coyote.http.response;

public class EmptyBody implements ResponseBody {

    public static final ResponseBody INSTANCE = new EmptyBody();

    @Override
    public String contentType() {
        return "";
    }

    @Override
    public byte[] bytes() {
        return new byte[0];
    }
}
