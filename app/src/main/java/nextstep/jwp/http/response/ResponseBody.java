package nextstep.jwp.http.response;

import java.nio.charset.StandardCharsets;

public class ResponseBody {

    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    public byte[] getByte() {
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
