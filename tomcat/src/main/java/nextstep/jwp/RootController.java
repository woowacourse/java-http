package nextstep.jwp;

import java.util.Map;
import org.apache.coyote.Controller;

public class RootController implements Controller {
    @Override
    public String process(Map<String, String> params) {
        return "/hello.html";
    }
}
