package org.apache.catalina.mapper;

import java.util.Map;
import java.util.regex.Pattern;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.servlets.LoginServlet;
import org.apache.catalina.servlets.RegisterServlet;
import org.apache.catalina.servlets.Servlet;

/**
 * 요청의 URL을 실제 정적 파일 절대 경로로 매핑해준다.(동적 파일만)
 */
public class Mapper {// TODO 싱글톤

    // TODO: 클래스 로더를 통해서 servlet을 가져옴?
    private static final Map<Pattern, Servlet> mapper = Map.of(
            Pattern.compile(".*\\..*"), new DefaultServlet(),
            Pattern.compile("/"), new DefaultServlet(),
            Pattern.compile("^/login$"), new LoginServlet(),
            Pattern.compile("^/register$"), new RegisterServlet()
    );

    public Servlet getServlet(String url) {
        Pattern key = mapper.keySet().stream()
                .filter(urlPattern -> urlPattern.matcher(url).matches())
                .findFirst().orElseThrow();// TODO servlet이 없다면 예외 발생
        return mapper.get(key);
    }
}
