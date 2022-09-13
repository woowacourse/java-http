package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.support.RequestHandler;

public class UserRequestHandler implements RequestHandler {

    @Override
    public Map<String, String> handle(final HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        String[] split = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String value : split) {
            String[] keyAndValue = value.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
        return values;
    }
}
