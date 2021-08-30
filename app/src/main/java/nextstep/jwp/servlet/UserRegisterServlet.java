package nextstep.jwp.servlet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.tomcat.Servlet;

public class UserRegisterServlet extends Servlet {

    public UserRegisterServlet() {
        this.requestMappingUri = "/register";
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        saveUser(httpRequest);

        httpResponse.addStartLine("HTTP/1.1", "302", "Found");
        httpResponse.addContentType("text/html;charset=utf-8");
        httpResponse.addLocation("index.html");
    }

    private void saveUser(HttpRequest httpRequest) {
        Map<String, String> parsedFormData = parseFormData(httpRequest);
        InMemoryUserRepository.save(
            new User(parsedFormData.get("account"), parsedFormData.get("password"), parsedFormData.get("email"))
        );

        User user = InMemoryUserRepository.findByAccount(parsedFormData.get("account"))
            .orElseThrow(IllegalArgumentException::new);
        log.debug("회원 가입 완료! 유저 정보 : {}", user);
    }

    private Map<String, String> parseFormData(HttpRequest httpRequest) {
        Map<String, String> params = new HashMap<>();
        List<String> split = Arrays.asList(httpRequest.getBody().split("&"));

        split.stream().forEach(it -> {
            String[] split1 = it.split("=");
            params.put(split1[0], split1[1]);
        });
        return params;
    }

}
