package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import util.ResourceFileLoader;

public class RegisterController extends AbstractController {

    private final String JAVA_SESSION_ID = "JSESSIONID";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        processRegisterPost(request.getRequestBody(), response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setResponseBody(ResourceFileLoader.loadStaticFileToString("/register.html"));
    }

    private void processRegisterPost(Map<String, String> requestBody, HttpResponse httpResponse) {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("http://localhost:8080/");

        HttpCookie httpCookie = new HttpCookie(JAVA_SESSION_ID, user.getId().toString());
        httpResponse.setCookie(httpCookie);
    }
}
