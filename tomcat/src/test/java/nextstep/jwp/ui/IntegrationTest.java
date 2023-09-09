package nextstep.jwp.ui;

import org.apache.catalina.connector.Connector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class IntegrationTest {

    private static Connector connector;
    protected int port;

    @BeforeEach
    void setUp() {
        connector = new Connector(0);
        connector.start();
        port = connector.getLocalPort();
    }

    @AfterEach
    void clear() {
        connector.stop();
    }
}
