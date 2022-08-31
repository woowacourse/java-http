package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 톰캣 서버
 */
public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    /**
     * 톰캣 서버 시작
     */
    public void start() {
        /**
         * 내부적으로 클라이언트측과 연결할 수 있는 서버측 소켓 생성
         */
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
