package org.apache.catalina.servlet;

import java.util.List;
import org.apache.catalina.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletMapper {

    private static final Logger log = LoggerFactory.getLogger(ServletMapper.class);
    private static final List<AbstractServlet> HANDLERS = List.of(
            new StaticServlet(),
            new LoginServlet(new HttpSessionManager()),
            new RegisterServlet(new HttpSessionManager())
    );

    public Servlet mappingServlet(String requestPath) {
        log.info("찾으려는 경로 : {}", requestPath);
        return HANDLERS.stream()
                .filter(handler -> handler.possibleHandle(requestPath))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 경로를 처리 할 수 있는 핸들러가 없습니다."));
    }
}
