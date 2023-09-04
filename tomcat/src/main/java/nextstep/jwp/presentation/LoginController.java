package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.RequestReader;
import org.apache.coyote.http11.Response;

import java.io.IOException;

import static org.apache.coyote.http11.Header.LOCATION;
import static org.apache.coyote.http11.StatusCode.FOUND;
import static org.apache.coyote.http11.StatusCode.OK;
import static org.apache.coyote.http11.StatusCode.UNAUTHORIZED;

public class LoginController implements Controller {

    private static final String INDEX = "/index.html";
    private static final String LOGIN = "/login.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        if (requestReader.getMethod().equalsIgnoreCase("GET") && requestReader.getRequestUrl().equals("/login")) {
            return loginPage(requestReader);
        }
        if (requestReader.getMethod().equalsIgnoreCase("POST") && requestReader.getRequestUrl().equals("/login")) {
            return tryLogin(requestReader);
        }
        if (requestReader.getMethod().equalsIgnoreCase("GET") && requestReader.getRequestUrl().equals("/register")) {
            return registerPage(requestReader);
        }
        if (requestReader.getMethod().equalsIgnoreCase("POST") && requestReader.getRequestUrl().equals("/register")) {
            return register(requestReader);
        }
        return null;
    }

    private Response loginPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, FOUND)
                .createResponseBodyByFile(LOGIN)
                .addHeader(LOCATION.getName(), LOGIN)
                .addBaseHeaders();
    }

    private Response tryLogin(RequestReader requestReader) throws IOException {
        try {
            return new Response(requestReader, FOUND)
                    .createResponseBodyByFile(INDEX)
                    .addHeader(LOCATION.getName(), INDEX)
                    .addBaseHeaders();
        } catch (IllegalArgumentException e) {
            return new Response(requestReader, UNAUTHORIZED)
                    .addBaseHeaders()
                    .createResponseBodyByFile(UNAUTHORIZED_HTML);
        }
    }

    private Response registerPage(RequestReader requestReader) throws IOException {
        return new Response(requestReader, OK)
                .addBaseHeaders()
                .createResponseBodyByFile(requestReader.getRequestUrl());
    }

    private Response register(RequestReader requestReader) throws IOException {
        InMemoryUserRepository.save(new User(
                requestReader.getBodyValue("account"),
                requestReader.getBodyValue("password"),
                requestReader.getBodyValue("email")
        ));
        return new Response(requestReader, FOUND)
                .createResponseBodyByFile(INDEX)
                .addHeader(LOCATION.getName(), INDEX)
                .addBaseHeaders();
    }
}
