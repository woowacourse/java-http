package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.MethodType;
import org.apache.coyote.render.PageRenderer;

public class UserRegisterHandler implements RequestHandler{
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";


    @Override
    public String handle(final String method, final String path, final Map<String, String> queryParams, HttpCookie cookie) {
        if (method.equals(MethodType.GET.getMethod())) {
            return handleRegisterGet();
        }
        if (method.equals(MethodType.POST.getMethod())) {
            return handleRegisterPost(method, queryParams);
        }
        throw new IllegalArgumentException("회원가입중 문제가 발생했습니다.");
    }

    private String handleRegisterGet() {
        return PageRenderer.createStaticFileResponse(HttpStatus.OK.getStatusCode(), "/register.html");
    }

    private String handleRegisterPost(String method, Map<String, String> queryParams) {
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
            return PageRenderer.createStaticFileResponse(HttpStatus.UNAUTHORIZED.getStatusCode(), "/401.html");
        }
        return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/");
    }


    private void validateAccountDuplicated(final Map<String, String> queryParams) {
        if (InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT)).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    }
}
