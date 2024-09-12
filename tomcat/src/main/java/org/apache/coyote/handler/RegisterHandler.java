package org.apache.coyote.handler;

import org.apache.coyote.NotFoundException;
import org.apache.http.HttpMethod;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterHandler extends Handler {
    private static final RegisterHandler INSTANCE = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RegisterHandler getInstance() {
        return INSTANCE;
    }

    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return processRegisterGetRequest(httpRequest);
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processRegisterPostRequest(httpRequest);
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private HttpResponse processRegisterGetRequest(final HttpRequest httpRequest) {
        return HttpResponse.builder()
                .addHeader(HttpHeaderName.LOCATION, "/register.html")
                .foundBuild();
    }

    private HttpResponse processRegisterPostRequest(final HttpRequest httpRequest) {
        String account = httpRequest.getFormBodyByKey("account");
        String email = httpRequest.getFormBodyByKey("email");
        String password = httpRequest.getFormBodyByKey("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.builder()
                .addHeader(HttpHeaderName.LOCATION, "/index.html")
                .foundBuild();
    }
}
