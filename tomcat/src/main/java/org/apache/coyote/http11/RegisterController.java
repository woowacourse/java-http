package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    private static final String INDEX_HTML = "/index.html";

    private final ResourceLoader resourceLoader;

    public RegisterController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return register(request);
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.status(OK)
                .body(resourceLoader.load("static/register.html"))
                .contentType(TEXT_HTML)
                .build();
    }

    private HttpResponse register(HttpRequest request) {
        FormData formData = FormData.from(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new HttpException(BAD_REQUEST, "이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.status(FOUND)
                .redirectUri(INDEX_HTML)
                .build();
    }
}
