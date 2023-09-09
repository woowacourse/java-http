package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class RegisterController extends AbstractController {

    private static final String REGISTER_URI = "/register.html";
    private static final String LOGIN_URI = "/login.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(REGISTER_URI));
        response
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .responseBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getValueOf("account");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            redirectLogin(request, response);
            return;
        }

        registerUser(request, requestBody, response);
    }

    private void redirectLogin(HttpRequest request, HttpResponse response) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(LOGIN_URI));
        response
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(LOGIN_URI)
                .responseBody(responseBody);
    }

    private void registerUser(HttpRequest request, RequestBody requestBody, HttpResponse response) {
        saveUser(requestBody);

        ResponseBody responseBody = new ResponseBody(FileReader.read(LOGIN_URI));
        response
                .statusLine(new StatusLine(request.getRequestLine().getVersion(), HttpStatus.CREATED))
                .contentType(ContentType.HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(LOGIN_URI)
                .responseBody(responseBody);
    }

    private void saveUser(RequestBody requestBody) {
        String account = requestBody.getValueOf("account");
        String password = requestBody.getValueOf("password");
        String email = requestBody.getValueOf("email");
        InMemoryUserRepository.save(new User(account, password, email));
    }

}
