package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;

public class LoginController implements Controller {

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        if (!requestReader.getParams().isEmpty()) {
            return tryLogin(requestReader);
        }
        return loginPage(requestReader);
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, StatusCode.OK)
                .createResponseBodyByFile(requestReader.getRequestUrl())
                .addBaseHeaders();
    }

    private Response tryLogin(RequestReader requestReader) throws IOException {
        try {
            login(requestReader);
            return new Response(requestReader, StatusCode.FOUND)
                    .createResponseBodyByFile("/index.html")
                    .addBaseHeaders()
                    .addHeader("Location", "/index.html");
        } catch (IllegalArgumentException e) {
            return new Response(requestReader, StatusCode.UNAUTHORIZED)
                    .createResponseBodyByFile("/401.html")
                    .addBaseHeaders();
        }
    }

    private void login(RequestReader requestReader) {
        Map<String, String> params = requestReader.getParams();
        InMemoryUserRepository.findByAccount(params.get("account"))
                              .filter(user -> user.checkPassword(params.get("password")))
                              .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 틀립니다."));
    }
}
