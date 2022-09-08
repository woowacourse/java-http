package nextstep.jwp.view;

import java.util.function.Function;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;

public class RegisterHandler implements Function<Request, Response> {

    private static final String BODY_DOES_NOT_EXIST = "존재하지 않는 바디 데이터입니다.";

    @Override
    public Response apply(final Request request) {
        final String account = request.getBodyValue("account")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        final String email = request.getBodyValue("email")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        final String password = request.getBodyValue("password")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        InMemoryUserRepository.save(new User(account, password, email));

        return Response.builder(HttpVersion.HTTP11, Status.FOUND)
                .setLocation("/")
                .setContentType(MediaType.TEXT_HTML)
                .build();
    }
}
