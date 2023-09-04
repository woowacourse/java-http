package nextstep.jwp;

import nextstep.jwp.controller.dto.Response;
import nextstep.jwp.db.SessionRepository;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Map;

public class UserFilter {
    public static Response verifyLogin(final String resources, final Map<String, String> httpStartLine) {
        if (!resources.equals("/register") && httpStartLine.keySet().stream().anyMatch(key -> key.equals("Cookie"))) {
            if (httpStartLine.get("Cookie").contains("JSESSIONID")) {
                final String id = httpStartLine.get("Cookie").split("=")[1];
                if (SessionRepository.islogin(id)) {
                    return new Response(HttpStatus.FOUND);
                }
            }
            return new Response(HttpStatus.NOT);
        }
        return new Response(HttpStatus.OK);
    }
}
