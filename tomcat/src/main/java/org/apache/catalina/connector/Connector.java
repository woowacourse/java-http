package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(Connector.class);

  private static final int DEFAULT_PORT = 8080;
  private static final int DEFAULT_ACCEPT_COUNT = 100;
  private static final int DEFAULT_N_THREADS = 100;

  private final ServerSocket serverSocket;
  private final ExecutorService executorService;
  private boolean stopped;

  public Connector() {
    this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_N_THREADS);
  }

  public Connector(final int port, final int acceptCount, final int maxCount) {
    this.serverSocket = createServerSocket(port, acceptCount);
    this.stopped = false;
    this.executorService = Executors.newFixedThreadPool(maxCount);
  }

  private ServerSocket createServerSocket(final int port, final int acceptCount) {
    try {
      final int checkedPort = checkPort(port);
      final int checkedAcceptCount = checkAcceptCount(acceptCount);
      return new ServerSocket(checkedPort, checkedAcceptCount);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void start() {
    final var thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
    this.stopped = false;
    log.info("Web Application Server started {} port.", this.serverSocket.getLocalPort());
  }

  @Override
  public void run() {
    // 클라이언트가 연결될때까지 대기한다.
    while (!this.stopped) {
      connect();
    }
  }

  private void connect() {
    try {
      process(this.serverSocket.accept());
    } catch (final IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void process(final Socket connection) {
    if (connection == null) {
      return;
    }
    final var processor = new Http11Processor(connection);
    this.executorService.execute(processor);
  }

  public void stop() {
    this.stopped = true;
    try {
      this.serverSocket.close();
    } catch (final IOException e) {
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
