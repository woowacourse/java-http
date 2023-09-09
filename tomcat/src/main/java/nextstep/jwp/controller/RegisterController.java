package nextstep.jwp.controller;

import java.util.Optional;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class RegisterController implements Controller {

    private static final String URI = "/register.html";

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.isMethod(HttpMethod.GET)) {
            ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
            String version = request.getRequestLine().getVersion();
            return HttpResponse.builder()
                    .statusLine(new StatusLine(version, HttpStatus.FOUND))
                    .contentType(ContentType.HTML.getValue())
                    .contentLength(responseBody.getValue().getBytes().length)
                    .responseBody(responseBody)
                    .build();
        }
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getValueOf("account");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
            return HttpResponse.builder()
                    .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND))
                    .contentType(ContentType.HTML.getValue())
                    .contentLength(responseBody.getValue().getBytes().length)
                    .redirect("http://localhost:8080/register.html")
                    .responseBody(responseBody)
                    .build();
        }
        String password = requestBody.getValueOf("password");
        String email = requestBody.getValueOf("email");
        InMemoryUserRepository.save(new User(account, password, email));
        ResponseBody responseBody = new ResponseBody(FileReader.read("/login.html"));
        return HttpResponse.builder()
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.CREATED))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect("http://localhost:8080/login.html")
                .responseBody(responseBody)
                .build();
    }

}
