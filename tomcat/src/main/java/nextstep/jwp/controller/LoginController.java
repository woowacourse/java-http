package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.ContentType;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class LoginController implements Controller {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";

    @Override
    public Response service(final Request request) throws Exception {
        final QueryParams queryParams = request.getUriQueryParams();
        if (queryParams.isEmpty()) {
            return Response.ofResource("/login.html");
        }

        if (!queryParams.containsKey(KEY_ACCOUNT) || !queryParams.containsKey(KEY_PASSWORD)) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }

        UserService.login(queryParams.get(KEY_ACCOUNT), queryParams.get(KEY_PASSWORD));
        return new Response(ContentType.HTML, StatusCode.FOUND, Map.of(Header.LOCATION, "/index.html"), "");
    }

    @Override
    public boolean canHandle(final Request request) {
        return request.isPath("/login");
    }
}
