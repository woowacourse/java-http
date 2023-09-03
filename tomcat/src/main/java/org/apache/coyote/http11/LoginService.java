package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;

public class LoginService {

    public static HttpResponse login(HttpRequest request) {
        Map<String, String> queryString = request.getQueryString();
        if (queryString.containsKey("account") && queryString.containsKey("password")) {
            String account = queryString.get("account");
            String password = queryString.get("password");
            String foundPassword = MemberRepository.getPassword(account);
            if (isValidMember(password, foundPassword)) {
                return new HttpResponse.Builder()
                        .setHttpStatusCode(HttpStatusCode.FOUND)
                        .setLocation("/index.html").build();
            }
        }
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.FOUND)
                .setLocation("/401.html").build();
    }

    private static boolean isValidMember(String password, String foundPassword) {
        return Objects.nonNull(foundPassword) && password.equals(foundPassword);
    }
}
