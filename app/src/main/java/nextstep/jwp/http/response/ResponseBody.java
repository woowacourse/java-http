package nextstep.jwp.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseBody {

    private static final Logger log = LoggerFactory.getLogger(ResponseBody.class);

    private String content = "";

    public ResponseBody() {
    }

    public ResponseBody(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
