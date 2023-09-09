package nextstep.jwp.controller;

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

    private static final String REGISTER_URI = "/register.html";
    private static final String LOGIN_URI = "/login.html";

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.getRequestLine().getHttpMethod().is(HttpMethod.GET)) {
            return doGet(request);
        }
        return doPost(request);
    }

    private HttpResponse doGet(HttpRequest request) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(REGISTER_URI));
        return HttpResponse.builder()
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getValueOf("account");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return redirectLogin(request);
        }

        return registerUser(request, requestBody, account);
    }

    private  HttpResponse redirectLogin(HttpRequest request) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(LOGIN_URI));
        return HttpResponse.builder()
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(LOGIN_URI)
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse registerUser(HttpRequest request, RequestBody requestBody, String account) {
        String password = requestBody.getValueOf("password");
        String email = requestBody.getValueOf("email");
        InMemoryUserRepository.save(new User(account, password, email));

        ResponseBody responseBody = new ResponseBody(FileReader.read(LOGIN_URI));
        return HttpResponse.builder()
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.CREATED))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(LOGIN_URI)
                .responseBody(responseBody)
                .build();
    }

}
