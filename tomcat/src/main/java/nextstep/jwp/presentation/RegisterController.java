package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;

import java.io.IOException;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.apache.coyote.http.HttpMethod.POST;

public class RegisterController implements Controller {

    private static final String KEY_VALUE_SEPARATOR = "=";

    @Override
    public String process(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getMethod() == GET) {
            return doGet(httpRequest, httpResponse);
        }
        if (httpRequest.getMethod() == POST) {
            return doPost(httpRequest, httpResponse);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    private String doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        return HttpResponseBuilder.buildStaticFileOkResponse(httpRequest, httpResponse, httpRequest.getPath() + ".html");
    }

    private String doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String[] splitRequestBody = httpRequest.getMessageBody().split("&");
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String email = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        email = email.replace("%40", "@");
        String password = splitRequestBody[2].split(KEY_VALUE_SEPARATOR)[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, "/index.html");
    }
}
