package nextstep.jwp.view;

import java.nio.charset.StandardCharsets;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.controller.AbstractController;
import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;
import org.apache.coyote.common.response.Status;
import org.utils.ResourceGenerator;

public class RegisterController extends AbstractController {

    private static final String BODY_DOES_NOT_EXIST = "존재하지 않는 바디 데이터입니다.";

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {

        final String account = request.getBodyValue("account")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        final String email = request.getBodyValue("email")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        final String password = request.getBodyValue("password")
                .orElseThrow(() -> new IllegalArgumentException(BODY_DOES_NOT_EXIST));
        InMemoryUserRepository.save(new User(account, password, email));

        response.setStatus(Status.FOUND)
                .setLocation(Url.ROOT.getValue())
                .setContentType(MediaType.TEXT_HTML);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        final String responseBody = ResourceGenerator.getStaticResource(Url.REGISTER.getValue());
        response.setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .setBody(responseBody);
    }
}
