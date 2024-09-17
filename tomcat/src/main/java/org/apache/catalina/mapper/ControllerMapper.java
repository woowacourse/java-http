package org.apache.catalina.mapper;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.catalina.servlet.DefaultServlet;
import org.apache.catalina.servlet.LoginServlet;
import org.apache.catalina.servlet.RegisterServlet;
import org.apache.catalina.servlet.Servlet;

/**
 * 요청의 URL을 실제 정적 파일 절대 경로로 매핑해준다.(동적 파일만)
 */
public class ControllerMapper {

    private static final Map<Pattern, Servlet> values = Map.of(
            Pattern.compile(".*\\..*"), DefaultServlet.getInstance(),
            Pattern.compile("/"), DefaultServlet.getInstance(),
            Pattern.compile("^/login$"), LoginServlet.getInstance(),
            Pattern.compile("^/register$"), RegisterServlet.getInstance()
    );

    private static ControllerMapper instance;

    private ControllerMapper() {}

    public static synchronized ControllerMapper getInstance() {
        if (instance == null) {
            instance = new ControllerMapper();
        }
        return instance;
    }

    public Servlet getServlet(String url) {
        return values.entrySet().stream()
                .filter(servlet -> servlet.getKey().matcher(url).matches())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("url(%s)에 매핑되는 서블릿이 존재하지 않습니다.".formatted(url)))
                .getValue();
    }
}
