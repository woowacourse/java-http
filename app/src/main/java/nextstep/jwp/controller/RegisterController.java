package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.builder.HttpResponseBuilder;
import nextstep.jwp.http.message.request.FormData;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        return HttpResponseBuilder.forward("/register.html")
                .build();
    }

    @Override
    protected HttpResponseMessage doPost(HttpRequestMessage httpRequestMessage) {
        MessageBody messageBody = httpRequestMessage.getBody();
        FormData formData = messageBody.toFormData();
        User user = User.createWithMap(formData.toMap());
        InMemoryUserRepository.save(user);

        return HttpResponseBuilder.redirectTemporarily("/index.html")
                .build();
    }
}
