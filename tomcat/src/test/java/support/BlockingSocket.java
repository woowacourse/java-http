package support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class BlockingSocket extends Socket {

    private final CountDownLatch workersStarted;
    private final CountDownLatch releaseWorkers;

    public BlockingSocket(
        final CountDownLatch workersStarted,
        final CountDownLatch releaseWorkers
    ) {
        this.workersStarted = workersStarted;
        this.releaseWorkers = releaseWorkers;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        workersStarted.countDown();
        try {
            releaseWorkers.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("입력 스트림 대기 중 인터럽트가 발생했습니다.", e);
        }
        return InputStream.nullInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return OutputStream.nullOutputStream();
    }
}
