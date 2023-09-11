package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.util.Map;

public class RegisterPostController extends AbstractController {

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isPOST() && request.isSamePath("/register");
    }


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("회원가입 정보가 입력되지 않았습니다.");
        }
        Map<String, String> parsedRequestBody = request.parseBody();
        InMemoryUserRepository.save(new User(
                Long.getLong(parsedRequestBody.get("id")),
                parsedRequestBody.get("account"),
                parsedRequestBody.get("password"),
                parsedRequestBody.get("email")
        ));
        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(0))
                .addLocation("/index.html")
                .build();
        response.updateResponse(HttpResponseStatus.FOUND, responseHeader, "");
    }
}
