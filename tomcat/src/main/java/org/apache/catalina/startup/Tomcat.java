package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// 서버 생명주기 관리
// Connector를 생성 및 실행한 뒤, System.in.read()를 통해 메인 스레드가 즉시 종료되지 않도록 콘솔 키 입력을 대기
public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        var connector = new Connector();
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
