# HTTP 서버 구현하기

## 요구사항
  
- [x] 학습 테스트 구현


### ✏️ GET /index.html 응답하기

- [x] 먼저 첫 번째 라인(Request URI) 을 읽어오자.

```java
String line = br.readLine();
log.debug("request line : {}", line);
```

- [x] 나머지 http request header는 어떻게 읽을까? 
  - [x] IOStreamTest를 참고해서 http request를 읽어오자.
  - [x] line이 null인 경우에 예외 처리를 해준다. 그렇지 않으면 무한 루프에 빠진다.

    ```java
    if (line == null) {
        return;
    }
    ```

  - [x] 헤더 마지막은 while (!"".equals(line)) {}으로 확인 가능하다.

    ```java
    while (!Objects.equals(line, "")) {
        line = br.readLine();
        log.debug("header : {}", line);
    }
    ```

  - [x] http request의 첫 번째 라인에서 request uri를 추출한다.
  - [x] line.split(" ");을 활용해서 문자열을 분리한다.
  - [x] FileTest를 참고해서 요청 url에 해당되는 파일을 resource 디렉토리에서 읽는다.

