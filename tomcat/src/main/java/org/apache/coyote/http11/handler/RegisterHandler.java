package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestData;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterHandler implements Handler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        if (httpRequest.getRequestMethod() == RequestMethod.GET) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.getRequestMethod() == RequestMethod.POST) {
            doPost(httpRequest, httpResponse);
            return;
        }

        throw new UnsupportedOperationException("get, post만 가능합니다");
    }

    private void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        RequestData requestData = httpRequest.getRequestData();
        saveUser(requestData);
        httpResponse.redirect("index.html");
    }

    private void doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        String fileData = FileReader.readFile("/register.html");
        httpResponse.ok(fileData, ContentType.HTML);
    }

    private void saveUser(RequestData requestData) {
        String account = requestData.find("account");
        String email = requestData.find("email");
        String password = requestData.find("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
