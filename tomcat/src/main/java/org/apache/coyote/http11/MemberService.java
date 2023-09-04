package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpFormatException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private MemberService() {
    }

    public static HttpResponse login(HttpRequest request) {
        String body = request.getBody();
        Map<String, String> requestParam = parseForm(body);
        if (doesParamContainsAccountAndPassword(requestParam)) {
            String account = requestParam.get(ACCOUNT);
            String password = requestParam.get(PASSWORD);
            Member foundMember = MemberRepository.findMember(account);
            Session session = addSession(foundMember);
            if (isValidMember(password, foundMember)) {
                log.info("account: {} logged in", foundMember.getAccount());
                return new HttpResponse.Builder()
                        .setHttpStatusCode(HttpStatusCode.FOUND)
                        .setLocation("/index.html")
                        .setCookie("JSESSIONID=" + session.getId())
                        .build();
            }
        }
        return new HttpResponse.Builder()
                .setHttpStatusCode(HttpStatusCode.FOUND)
                .setLocation("/401.html").build();
    }

    public static HttpResponse register(HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        Map<String, String> requestParam = parseForm(body);
        if (doesParamContainsAccountPasswordAndEmail(requestParam)) {
            Member member = new Member(requestParam.get(ACCOUNT), requestParam.get(PASSWORD),
                    requestParam.get(EMAIL));
            if (!MemberRepository.exists(member.getAccount())) {
                MemberRepository.register(member);
                Session session = addSession(member);
                log.info("account: {} registered", member.getAccount());
                return new HttpResponse.Builder()
                        .setHttpStatusCode(HttpStatusCode.FOUND)
                        .setLocation("/index.html")
                        .setCookie("JSESSIONID=" + session.getId())
                        .build();
            }
        }
        return ResponseUtil.buildBadRequest();
    }

    private static Session addSession(Member foundMember) {
        Session session = new Session();
        session.setAttribute("member", foundMember);
        SessionManager.add(session);
        return session;
    }

    private static boolean doesParamContainsAccountPasswordAndEmail(
            Map<String, String> requestParam) {
        return doesParamContainsAccountAndPassword(requestParam) && requestParam.containsKey(EMAIL);
    }

    private static boolean doesParamContainsAccountAndPassword(Map<String, String> requestParam) {
        return requestParam.containsKey(ACCOUNT) && requestParam.containsKey(PASSWORD);
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
