## 1단계 기능 요구 사항 - HTTP 서버 구현하기
### 1. GET /index.html 응답하기
- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다.
- [x] `Http11ProcessorTest` 테스트 클래스의 모든 테스트를 통과한다.

브라우저에서 요청한 HTTP Request Header
```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

### 2. CSS 지원하기
- [x] 사용자가 인덱스 페이지를 열였을 때 css 파일도 호출하도록 기능을 추가한다.
```
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
```

### 3. Query String 파싱
- [x] `http://localhost:8080/login?account=gugu&password=password` 으로 접속하면 로그인 페이지(login.html)를 보여준다.
- [x] 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 **콘솔창에 로그**로 회원을 조회한 결과가 나온다. 

## 힌트
### 1. GET /index.html 응답하기
1. 먼저 첫 번째 라인(Request URI) 을 읽어오자.
  - 첫 줄만 읽어와도 첫 미션은 해결 가능하다.
2. 나머지 http request header는 어떻게 읽을까?
  - IOStreamTest를 참고해서 http request를 읽어오자.
  - line이 null인 경우에 예외 처리를 해준다. 그렇지 않으면 무한 루프에 빠진다.
    - if (line == null) { return; }
  - 헤더 마지막은 while (!"".equals(line)) {}으로 확인 가능하다.
3. http request의 첫 번째 라인에서 request uri를 추출한다.
  - line.split(" ");을 활용해서 문자열을 분리한다.
4. FileTest를 참고해서 요청 url에 해당되는 파일을 resource 디렉토리에서 읽는다.

### 2. CSS 지원하기
1. 응답 헤더의 Content-Type을 text/html로 보내면 브라우저는 HTML 파일로 인식하기 때문에 CSS가 정상적으로 동작하지 않는다.
2. CSS인 경우 응답 헤더의 Content-Type을 text/css로 전송한다.
3. Content-Type은 확장자를 통해 구분할 수도 있으며, 요청 헤더의 Accept를 활용할 수도 있다.

**생각해보기 🤔**  
- 개발자 도구 > 네트워크 항목을 열어 놓고 페이지를 리로드하니 URL로 접속하여 받은 응답 외에도 css, js, favicon 같은 파일도 호출되고 있다.
- index.html 페이지만 접근했는데 CSS 같은 정적 파일들은 어떻게 호출된걸까?

### 3. Query String 파싱
1. http request의 첫 번째 라인에서 request uri를 추출한다.
2. login.html 파일에서 태그에 name을 추가해준다.
3. 추출한 request uri에서 접근 경로와 이름=값으로 전달되는 데이터를 추출해서 User 객체를 조회하자.

```java
String uri = "/login?account=gugu&password=password";
int index = uri.indexOf("?");
String path = uri.substring(0, index);
String queryString = uri.substring(index + 1);
```
   
4. InMemoryUserRepository를 사용해서 미리 가입되어 있는 회원을 조회하고 로그로 확인해보자.

## 2단계 기능 요구사항 - 로그인 구현하기
### 1. HTTP Status Code 302
- [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트 한다.
- [x] 로그인에 실패하면 `401.html`로 리다이렉트한다.

### 2. POST 방식으로 회원가입
- [x] `http://localhost:8080/register` 으로 접속하면 GET을 사용해서 회원가입 페이지(register.html)를 보여준다.
- [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
- [x] 회원가입을 완료하면 index.html로 리다이렉트한다.
- [x] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경한다.

### 3. Cookie에 JSESSIONID 값 저장하기
> 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.  
HTTP 서버는 세션을 사용해서 서버에 로그인 여부를 저장한다.  
세션을 구현하기 전에 먼저 쿠키를 구현해본다.  
자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.  
- [x] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달한다.
- [x] 클라이언트 요청 헤더의 Cookie 필드에 값이 추가되는 것을 확인한다.
- [x] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.

### 4. Session 구현하기
> 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
- [x] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
- [x] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
