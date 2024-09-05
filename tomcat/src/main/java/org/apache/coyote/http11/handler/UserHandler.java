package org.apache.coyote.http11.handler;

import java.io.IOException;
import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class UserHandler extends AbstractRequestHandler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = request.getSession(true);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.setResponseBody("로그인 상태가 아닙니다.");
            response.write();
            return;
        }

        response.setResponseBody("User: " + user);
        response.write();
    }
}
