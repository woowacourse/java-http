package nextstep.jwp;

import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class RootController implements Controller {
    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/") && request.isGet();
    }

    @Override
    public String process(Map<String, String> params) {
        return "/hello.html";
    }
}
