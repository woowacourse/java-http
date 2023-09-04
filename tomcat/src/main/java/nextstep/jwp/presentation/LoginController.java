package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

public class LoginController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        if (requestReader.getMethod().equalsIgnoreCase("POST") && requestReader.getRequestUrl().equals("/login")) {
            return tryLogin(requestReader);
        }
        if (requestReader.getMethod().equalsIgnoreCase("GET") && requestReader.getRequestUrl().equals("/login")) {
            return loginPage(requestReader);
        }
        if (requestReader.getMethod().equalsIgnoreCase("GET") && requestReader.getRequestUrl().equals("/register")) {
            return registerPage(requestReader);
        }

        return loginPage(requestReader);
    }

    private Response registerPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, StatusCode.OK)
                .addBaseHeaders()
                .createResponseBodyByFile(requestReader.getRequestUrl());
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, StatusCode.OK)
                .addBaseHeaders()
                .createResponseBodyByFile(requestReader.getRequestUrl());
    }

    private Response tryLogin(RequestReader requestReader) throws IOException {
        try {
            login(requestReader);
            return new Response(requestReader, StatusCode.FOUND)
                    .createResponseBodyByFile("/index.html")
                    .addHeader("Location", "/index.html")
                    .addBaseHeaders();
        } catch (IllegalArgumentException e) {
            return new Response(requestReader, StatusCode.UNAUTHORIZED)
                    .addBaseHeaders()
                    .createResponseBodyByFile("/401.html");
        }
    }

    private void login(RequestReader requestReader) {
        InMemoryUserRepository.findByAccount(requestReader.getBodyValue("account"))
                              .filter(user -> user.checkPassword(requestReader.getBodyValue("password")))
                              .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다."));
    }
}
