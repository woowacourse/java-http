package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.annotation.Route;

@org.qupring.annotation.Controller
public class UserController {

    @Route(path = "/login", method = org.apache.http.HttpMethod.GET)
    public String login(HttpRequest request, HttpResponse response) {
        User user = InMemoryUserRepository.findByAccount(request.getQueryParams().get("account"))
                .orElseThrow();

        System.out.println(user.toString());
        return "login";
    }
}
