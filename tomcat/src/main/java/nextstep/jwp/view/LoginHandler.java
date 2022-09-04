package nextstep.jwp.view;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.Charset;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Response.ResponseBuilder;
import org.apache.coyote.common.response.Status;
import org.utils.ResourceGenerator;

public class LoginHandler implements Function<Request, Response> {

    @Override
    public Response apply(final Request request) {
        final String password = request.getQueryStringValue("password");
        final User user = findUser(request);

        String responseBody = ResourceGenerator.getStaticResource("/index");
        Status statusCode = Status.FOUND;
        if (!user.checkPassword(password)) {
            responseBody = ResourceGenerator.getStaticResource("/401");
            statusCode = Status.UNAUTHORIZED;
        }

        return new ResponseBuilder(HttpVersion.HTTP11, statusCode)
                .setContentType(MediaType.TEXT_HTML, Charset.UTF8)
                .setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .setBody(responseBody)
                .build();
    }

    private User findUser(final Request request) {
        final String userAccount = request.getQueryStringValue("account");
        return InMemoryUserRepository.findByAccount(userAccount)
                .orElseThrow(IllegalArgumentException::new);
    }
}
