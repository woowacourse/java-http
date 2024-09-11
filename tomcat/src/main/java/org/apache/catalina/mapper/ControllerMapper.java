package org.apache.catalina.mapper;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.catalina.controller.DefaultController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.Controller;

/**
 * 요청의 URL을 실제 정적 파일 절대 경로로 매핑해준다.(동적 파일만)
 */
public class ControllerMapper {

    private static final Map<Pattern, Controller> values = Map.of(
            Pattern.compile(".*\\..*"), new DefaultController(),
            Pattern.compile("/"), new DefaultController(),
            Pattern.compile("^/login$"), new LoginController(),
            Pattern.compile("^/register$"), new RegisterController()
    );

    private static ControllerMapper instance;

    private ControllerMapper() {}

    public static ControllerMapper getInstance() {
        if (instance == null) {
            instance = new ControllerMapper();
        }
        return instance;
    }

    public Controller getServlet(String url) {
        return values.entrySet().stream()
                .filter(servlet -> servlet.getKey().matcher(url).matches())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("url(%s)에 매핑되는 서블릿이 존재하지 않습니다.".formatted(url)))
                .getValue();
    }
}
