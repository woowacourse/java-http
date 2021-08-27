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

### Query String 파싱
  - [x] http request의 첫 번째 라인에서 request uri를 추출한다.
    
    ```java
    String uri = line.split(" ")[1];
    ```    

  - [ ] login.html 파일에서 태그에 name을 추가해준다. -> ?
  - [x] 추출한 request uri에서 접근 경로와 이름=값으로 전달되는 데이터를 추출해서 User 객체를 만든다.
  

### POST 방식으로 회원가입

  - [x] while 문으로 http request header를 읽고 나서 request body를 읽어온다.
    - request header의 Content-Length가 request body의 length다.
  
  - [x] POST로 전달하면 GET과 다르게 http request body에 데이터가 담긴다.
  - [x] login.html도 form 태그를 수정한다.
  - [x] InMemoryUserRepository에서 save 메서드를 사용해서 가입 완료 처리한다.
  - [x] 이상 없으면 리다이렉트

  