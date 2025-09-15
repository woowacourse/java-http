package support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConnector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TestConnector.class);

    private final ServerSocket serverSocket;
    private boolean stopped;

    public TestConnector(final int port, final int acceptCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            return new ServerSocket(port, acceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            Socket connection = serverSocket.accept();
            process(connection);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }

        // 새 스레드 생성 대신 직접 처리 + 지연 추가
//        try {
//            Thread.sleep(1000); // 1초 대기
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        var processor = new TestHttp11Processor(connection);
        processor.run();
//        new Thread(processor).start();
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
