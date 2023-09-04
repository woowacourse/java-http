package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    public static HttpResponse login(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> requestParam = parseForm(body);
        if (requestParam.containsKey("account") && requestParam.containsKey("password")) {
            String account = requestParam.get("account");
            String password = requestParam.get("password");
            Member foundMember = MemberRepository.findMember(account);
            if (isValidMember(password, foundMember)) {
                log.info("foundMember.getAccount() = " + foundMember.getAccount() + " logged in");
                return new HttpResponse.Builder()
                        .setHttpStatusCode(HttpStatusCode.FOUND)
                        .setLocation("/index.html").build();
            }
        }
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.FOUND)
                .setLocation("/401.html").build();
    }

    public static HttpResponse register(HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        Map<String, String> requestParam = parseForm(body);
        // TODO: 중복 계정 혹은 형식 관련 예외처리
        if (requestParam.containsKey("account") && requestParam.containsKey("password") && requestParam.containsKey("email")) {
            Member member = new Member(requestParam.get("account"), requestParam.get("password"),
                    requestParam.get("email"));
            MemberRepository.register(member);
            log.info("member.getAccount() = " + member.getAccount() + " registered");

            return new HttpResponse.Builder()
                    .setHttpStatusCode(HttpStatusCode.FOUND)
                    .setLocation("/index.html").build();
        }
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.BAD_REQUEST).build();
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> queryString = new HashMap<>();
        String[] queryStrings = body.split("&");
        for (String singleQueryString : queryStrings) {
            String[] keyValue = singleQueryString.split("=");
            if (keyValue.length != 2) {
                throw new HttpFormatException();
            }
            queryString.put(keyValue[0], keyValue[1]);
        }
        return queryString;
    }

    private static boolean isValidMember(String password, Member foundMember) {
        return Objects.nonNull(foundMember) && password.equals(foundMember.getPassword());
    }
}
