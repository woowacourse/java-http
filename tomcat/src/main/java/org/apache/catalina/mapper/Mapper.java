package org.apache.catalina.mapper;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.servlets.LoginServlet;
import org.apache.catalina.servlets.RegisterServlet;
import org.apache.catalina.servlets.Servlet;

/**
 * 요청의 URL을 실제 정적 파일 절대 경로로 매핑해준다.(동적 파일만)
 */
public class Mapper {

    private static final Map<Pattern, Servlet> values = Map.of(
            Pattern.compile(".*\\..*"), new DefaultServlet(),
            Pattern.compile("/"), new DefaultServlet(),
            Pattern.compile("^/login$"), new LoginServlet(),
            Pattern.compile("^/register$"), new RegisterServlet()
    );

    private static Mapper instance;

    private Mapper() {}

    public static Mapper getInstance() {
        if (instance == null) {
            instance = new Mapper();
        }
        return instance;
    }

    public Servlet getServlet(String url) {
        Pattern key = values.keySet().stream()
                .filter(urlPattern -> urlPattern.matcher(url).matches())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("url(%s)에 매핑되는 서블릿이 존재하지 않습니다.".formatted(url)));
        return values.get(key);
    }
}
