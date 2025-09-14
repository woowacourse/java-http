package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.FrontController;
import org.apache.coyote.http11.processor.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {
    private static final FrontController frontController = new FrontController();
    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    //NumberOfCores  NumberOfLogicalProcessors
    //4              8
    //이게 내 컴퓨터 상태니까, 이론상 8코어 사용 가능한거네 하이퍼 스레딩 덕분에
    //db가 없고 다 인메모리니까, disk i/o는 없을 것 같은데 그러면 wait time을 0이라고 했을 때
    //8 곱하기 (1 + 0)이니까 그러면 8이네
    //Optimal Threads = Number of Cores * (1 + Wait time / Service time)
    private static final int DEFAULT_CORE_POOL_SIZE = 8;
    private static final int DEFAULT_MAX_THREADS = 16;

    //ExecutorService로 스레드 풀을 통해 작업을 실행할 수 있도록 도와주는 인터페이스임
    //매번, thread.start()하는 대신, executor.submit(Runnable) 또는 .execute(Runnable)호출
    //그러면, 미리 준비된 스레드가 작업을 처리함
    private final ExecutorService threadPool;
    private final ServerSocket serverSocket;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_THREADS);
    }

    //예를 들어서, 현재 실행 중인 스레드 수 < corePoolSize면 새 스레드를 생성하는 것임.
    //큐가 비어있지 않으면 -> 큐에 작업 추가 (근데 이 조건이, coreThread가 다 일을 하고 있어야함)
    //큐가 꽉 찼는데, 현재 스레드 수 < maximumPoolSize -> 새 스레드 생성(이게 초과쓰레드)
    //위 조건 다 안 되면 즉시 거절
    public Connector(final int port, final int acceptCount, final int corePoolSize, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.threadPool = new ThreadPoolExecutor(
                corePoolSize, //풀에서 항상 유지하려는 최소 스레드 수
                maxThreads, //풀에서 동시에 존재ㅐ할 수 있는 최대 스레드 수
                30L, //여기서는 그러면 101번부터 200번이 초과된 스레드네
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(acceptCount) //대기 큐인데, acceptCount만큼 요청을 버퍼링
        );
    }

    //그러면, corePoolSize만큼 스레드가 항상 대기 중이고, 그 스레드 100개가 다 작업을 하고 있지 않는 이상 큐에 작업이 쌓이지는 않겠군
    //큐가 꽉차면 부하가 장난아닌거네 ㄷㄷ
    //그러면, 큐가 꽉차면 그때부터 스레드 생성을 시작하니까 101번부터
    //그러면, 초과된 스레드는 바로 큐에서 작업을 할당 받겠네
    //그러다가 작업이 할당되지 않은 초과된 스레드는 30초뒤 증발
    //근데, 만약에 30s를 지정하지 않으면 초과된 스레드는 밀린 작업을 처리했음
    //근데, 삭제되지 않고 남아있는 것임 그러면 메모리를 계속 차지하는 거임 레전드
    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);

            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        Thread thread = new Thread(this);
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
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        threadPool.submit(new Http11Processor(connection, frontController));
    }

    public void stop() {
        stopped = true;
        threadPool.shutdown(); //그러면, 새로운 작업은 막고, 이미 진행중인 작업은 실행
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }

        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
