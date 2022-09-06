# 톰캣 구현하기

## 기능 요구사항

2 단계 요구사항

- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [x] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
- [x] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [x] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.

3 단계 요구사항

- [x] HTTP Request, HTTP Response 클래스로 나눠서 구현했다.
- [x] Controller 인터페이스와 RequestMapping 클래스를 활용하여 if절을 제거했다.

### 정적 파일 서빙

```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive

```

start line = `HTTP Method + URL + HTTP version`

* HTTP 메서드가 GET이고 URL이 올바른 경우 해당하는 경로의 자원을 응답한다.


#### 예외

* HTTP 메서드가 올바르지 않은 경우: 405 Method Not Allowed 응답
* 존재하지 않는 자원을 요청하는 경우: 404 Not Found 응답과 함께 404.html을 응답


### TODO

[x] requestURI를 이용하여 resourceLocator 파일 찾기
[x] 문서 이외의 확장자 처리
[x] query param 처리
[ ] login 페이지 응답 기능과 로그인 기능 분리
[ ] Content-Type 응답 리팩터링 (resource.getMimeType().getValue() 호출하는 부분)

핸들러 매핑

Path를 입력받아 알맞은 핸들러를 반환


### 책임 분리

#### Http11Processor

* Socket Connection이 들어오면 이를 처리한다.
* 입력을 stream에서 읽어들인다.
* Http 요청을 생성한다.
* Http 응답을 생성한다.
* 출력을 stream에 쓴다.

#### ResourceLocator

* 자원의 위치를 입력받아 해당 자원을 반환한다.
