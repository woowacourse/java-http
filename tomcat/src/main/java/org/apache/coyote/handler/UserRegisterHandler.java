package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.MethodType;
import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.util.HeaderParser;

public class UserRegisterHandler implements RequestHandler{
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";


    @Override
    public String handle(final HttpRequest httpRequest) {

        if (httpRequest.method().equals(MethodType.GET.getMethod())) {
            return handleRegisterGet(httpRequest);
        }
        if (httpRequest.method().equals(MethodType.POST.getMethod())) {
            return handleRegisterPost(httpRequest.version(), httpRequest.method(), httpRequest.queryParams());
        }
        throw new IllegalArgumentException("회원가입중 문제가 발생했습니다.");
    }

    private String handleRegisterGet(HttpRequest httpRequest) {
        return PageRenderer.createStaticFileResponse(httpRequest.version(),HttpStatus.OK.getStatusCode(),httpRequest.path());
    }

    private String handleRegisterPost(String version, String method, Map<String, String> queryParams) {
        try {
            if (method.equals(MethodType.POST.getMethod())) {
                validateAccountDuplicated(queryParams);

                User beforeSaveUser = new User(
                        queryParams.get(ACCOUNT),
                        queryParams.get(PASSWORD),
                        queryParams.get(EMAIL)
                );
                InMemoryUserRepository.save(beforeSaveUser);
            }
        } catch (IllegalArgumentException e) {
            return PageRenderer.createStaticFileResponse(version, HttpStatus.UNAUTHORIZED.getStatusCode(), "/401.html");
        }
        return PageRenderer.sendRedirect(version, HttpStatus.FOUND.getStatusCode(),
                HeaderParser.createRedirectHeaders("/", null,null));
    }


    private void validateAccountDuplicated(final Map<String, String> queryParams) {
        if (InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
