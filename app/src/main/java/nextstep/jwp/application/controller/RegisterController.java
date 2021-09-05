package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.model.User;
import nextstep.jwp.framework.controller.AbstractController;
import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.request.FormData;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        return HttpResponseBuilder.staticResource("/register.html")
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
