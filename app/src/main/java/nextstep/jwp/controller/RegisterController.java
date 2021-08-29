package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.model.User;
import nextstep.jwp.http.utils.HttpParseUtils;

import java.util.Map;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        httpRequestMessage.changeRequestUri("/register.html");
    }

    @Override
    protected void doPost(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        MessageBody messageBody = httpRequestMessage.getBody();
        Map<String, String> formData = HttpParseUtils.extractFormData(messageBody);
        User user = User.createWithMap(formData);
        InMemoryUserRepository.save(user);

        httpRequestMessage.changeRequestUri("redirect:/index.html");
    }
}
