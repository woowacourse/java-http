package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.annotation.Route;

// 실험용 컨트롤러
public class UserController {

    @Route(path = "/login", method = HttpMethod.POST)
    public void login(HttpRequest request, HttpResponse response) {
        User user = InMemoryUserRepository.findByAccount(request.getQueryParams().get("account"))
                .orElseThrow();
        String inputPassword = request.getQueryParams().get("password");

        if(!user.checkPassword(inputPassword)) {
            response.setStatus(404);
        }

    }


}
