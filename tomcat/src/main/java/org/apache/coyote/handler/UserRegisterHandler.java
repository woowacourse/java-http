package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.util.HeaderParser;

public class UserRegisterHandler extends AbstractController {
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        PageRenderer.createStaticFileResponse(request.version(), HttpStatus.OK.getStatusCode(), request.path(),
                response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> queryParams = request.queryParams();
        String version = request.version();

        try {
            validateAccountDuplicated(queryParams);
            User beforeSaveUser = new User(
                    queryParams.get(ACCOUNT),
                    queryParams.get(PASSWORD),
                    queryParams.get(EMAIL)
            );
            InMemoryUserRepository.save(beforeSaveUser);

        } catch (IllegalArgumentException e) {
            PageRenderer.createStaticFileResponse(
                    version,
                    HttpStatus.UNAUTHORIZED.getStatusCode(),
                    "/401",
                    response
            );
            return;
        }
        PageRenderer.sendRedirect(
                version,
                HttpStatus.FOUND.getStatusCode(),
                HeaderParser.createRedirectHeaders("/", null, null),
                response
        );
    }

    private void validateAccountDuplicated(final Map<String, String> queryParams) {
        if (InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
