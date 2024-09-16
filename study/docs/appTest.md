## threads.max, max-connections, accept-count

### threads.max

- 동시에 요청을 처리할 수 있는 Thread 수
- threads.max = 2 면 2개씩 요청이 처리됨

### max-connections

- 서버가 요청을 처리할 수 있는 Connection의 수
- threads.max = 2이고 maxConnections = 10이면 8개는 대기하고 2개는 처리됨
- threads.max = 2이고 maxConnections = 1이면 9개는 대기하고 1개는 처리됨

### accept-count

- 서버가 요청을 처리할 수 있는 Connection의 수를 초과했을 때 대기할 수 있는 요청의 수
    - 3-hand-shake가 완료된 상태
    - OS에서 제공하는 Queue에 대기
- 100개의 요청이 들어왔을때 threads.max = 2, maxConnections = 10, acceptCount = 5이면
    - 2개는 바로 처리
    - 8개는 대기
    - 5개는 OS의 큐에서 대기
    - 85개는 거부됨 (connect timeout)

## max thread 실험

- timeout(request timeout) = 10000ms (10초)
- 요청에서 sleep 시간 = 500ms (정확하지 않을 수 있음)

### 요청 10개, threads.max = 2, maxConnections = 10

- 500ms 마다 2개씩 처리됨
- 8개는 대기

**서버**

```shell
2024-09-13T15:20:39.268+09:00  INFO 15749 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 2
2024-09-13T15:20:39.268+09:00  INFO 15749 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
2024-09-13T15:20:39.794+09:00  INFO 15749 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 3
2024-09-13T15:20:39.797+09:00  INFO 15749 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 4
2024-09-13T15:20:40.300+09:00  INFO 15749 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 5
2024-09-13T15:20:40.304+09:00  INFO 15749 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 6
2024-09-13T15:20:40.808+09:00  INFO 15749 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 7
2024-09-13T15:20:40.809+09:00  INFO 15749 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 8
2024-09-13T15:20:41.315+09:00  INFO 15749 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 10
2024-09-13T15:20:41.315+09:00  INFO 15749 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 9
```

---

### 요청 10개, threads.max = 5, maxConnections = 10

- 500ms 마다 5개씩 처리됨
- 5개는 대기

**서버**

```shell
2024-09-13T15:23:03.078+09:00  INFO 15795 --- [nio-8080-exec-4] thread.stage2.SampleController           : http call count : 1
2024-09-13T15:23:03.082+09:00  INFO 15795 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
2024-09-13T15:23:03.082+09:00  INFO 15795 --- [nio-8080-exec-3] thread.stage2.SampleController           : http call count : 4
2024-09-13T15:23:03.078+09:00  INFO 15795 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
2024-09-13T15:23:03.098+09:00  INFO 15795 --- [nio-8080-exec-5] thread.stage2.SampleController           : http call count : 5
2024-09-13T15:23:03.608+09:00  INFO 15795 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 6
2024-09-13T15:23:03.610+09:00  INFO 15795 --- [nio-8080-exec-5] thread.stage2.SampleController           : http call count : 7
2024-09-13T15:23:03.611+09:00  INFO 15795 --- [nio-8080-exec-3] thread.stage2.SampleController           : http call count : 8
2024-09-13T15:23:03.612+09:00  INFO 15795 --- [nio-8080-exec-4] thread.stage2.SampleController           : http call count : 9
2024-09-13T15:23:03.612+09:00  INFO 15795 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 10
```

---

## 대기/타임아웃 테스트

- timeout(request timeout) = 1000ms
- 요청에서 sleep 시간 = 500ms (정확하지 않을 수 있음)

### 요청 10개, threads.max = 2, maxConnections = 10, acceptCount = 5

- 2개는 바로 처리
- 8개는 대기 (1초가 지나서 request timeout)

**서버**

```shell
2024-09-13T15:00:46.827+09:00  INFO 15640 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 1
2024-09-13T15:00:46.827+09:00  INFO 15640 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
2024-09-13T15:00:47.353+09:00  INFO 15640 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 3
2024-09-13T15:00:47.356+09:00  INFO 15640 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 4
2024-09-13T15:00:47.861+09:00  INFO 15640 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 5
2024-09-13T15:00:47.862+09:00  INFO 15640 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 6
2024-09-13T15:00:48.364+09:00  INFO 15640 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 7
2024-09-13T15:00:48.367+09:00  INFO 15640 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 8
2024-09-13T15:00:48.870+09:00  INFO 15640 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 9
2024-09-13T15:00:48.873+09:00  INFO 15640 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 10
```

**클라이언트**

```shell
Exception in thread "Thread-5" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-6" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-7" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-8" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-9" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-10" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-11" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-12" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
```

---

### 요청 10개, threads.max = 2, maxConnections = 4, acceptCount = 5

- 2개는 바로 처리
- 4개는 대기 (1초가 지나서 request timeout)
- 5개는 OS의 큐애서 대기 (1초가 지나서 request timeout)
- 1개는 거부됨 (connect timeout)

**서버**

```shell
2024-09-13T15:07:37.173+09:00  INFO 15696 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
2024-09-13T15:07:37.173+09:00  INFO 15696 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 2
2024-09-13T15:07:37.704+09:00  INFO 15696 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 3
2024-09-13T15:07:37.705+09:00  INFO 15696 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 4
2024-09-13T15:07:38.208+09:00  INFO 15696 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 5
2024-09-13T15:07:38.213+09:00  INFO 15696 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 6
2024-09-13T15:07:38.714+09:00  INFO 15696 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 7
2024-09-13T15:07:38.715+09:00  INFO 15696 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 8
2024-09-13T15:07:39.221+09:00  INFO 15696 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 9
```

**클라이언트**

```shell
Exception in thread "Thread-3" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-6" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-7" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-8" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-9" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-10" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-11" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-12" java.lang.RuntimeException: java.net.http.HttpConnectTimeoutException: HTTP connect timed out
```

---

### 요청 10개, threads.max = 2, maxConnections = 1, acceptCount = 5

- thread pool의 크기는 2개지만 maxConnections가 1이므로 1개만 처리됨
- 큐에서 대기 하지 않음
- OS의 큐에서 5개가 대기 (1초가 지나서 request timeout)
- 4개는 거부됨 (connect timeout)

**서버**

```shell
2024-09-13T15:11:00.457+09:00  INFO 15706 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 1
2024-09-13T15:11:02.190+09:00  INFO 15706 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 2
2024-09-13T15:11:02.698+09:00  INFO 15706 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 3
2024-09-13T15:11:03.204+09:00  INFO 15706 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 4
2024-09-13T15:11:03.711+09:00  INFO 15706 --- [nio-8080-exec-2] thread.stage2.SampleController           : http call count : 5
2024-09-13T15:11:04.218+09:00  INFO 15706 --- [nio-8080-exec-1] thread.stage2.SampleController           : http call count : 6

```

**클라이언트**

```shell
Exception in thread "Thread-4" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-3" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-6" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-7" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-8" java.lang.RuntimeException: java.net.http.HttpTimeoutException: request timed out
Exception in thread "Thread-9" java.lang.RuntimeException: java.net.http.HttpConnectTimeoutException: HTTP connect timed out
Exception in thread "Thread-10" java.lang.RuntimeException: java.net.http.HttpConnectTimeoutException: HTTP connect timed out
Exception in thread "Thread-11" java.lang.RuntimeException: java.net.http.HttpConnectTimeoutException: HTTP connect timed out
Exception in thread "Thread-12" java.lang.RuntimeException: java.net.http.HttpConnectTimeoutException: HTTP connect timed out
```
