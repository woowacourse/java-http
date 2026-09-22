# Tomcat 구현하기

## 1단계 - HTTP 서버 구현하기

> 간단한 HTTP 서버를 만들어보자.
> 저장소에서 소스코드를 받아와서 메인 클래스를 실행하면 HTTP 서버가 실행된다.
> 웹브라우저로 로컬 서버(http://localhost:8080) 에 접속하면 Hello world!가 보인다.
> 정상 동작을 확인했으면 새로운 기능을 추가해보자.

### 기능 요구 사항

#### 1. GET /index.html 응답하기

- [x] 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다.
- [x] Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과한다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
```

#### 2. CSS 지원하기

- [x] 사용자가 페이지를 열었을 때 CSS 파일도 호출한다.

브라우저에서 요청한 HTTP Request Header는 다음과 같다.

```http request
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
``` 

#### 3. Query String 파싱

- [x] http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여준다.
- [x] 로그인 페이지 접속 시 Query String을 파싱해서 아이디, 비밀번호 일치를 검사한다.
- [x] 일치 한다면 콘솔창에 로그로 회원을 조회한 결과가 나오게 한다.

## 2단계 - 로그인 구현하기

> 1단계에서 HTML 파일을 출력하는 간단한 웹서버를 만들었다.
> 이제 로그인과 회원가입 기능을 추가해보자.
> 로그인에 필요한 쿠키와 세션도 같이 구현해보자.

### 기능 요구 사항

#### 1. HTTP Status Code 302

- [x] 로그인 여부에 따라 다른 페이지로 이동한다.
    - [x] 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
    - [x] 로그인에 실패하면 401.html로 리다이렉트한다.

`InMemoryUserRepository`에는 다음과 같은 정보가 들어있다.

```text
account: "gugu",
password: "password"
```

#### 2. POST 방식으로 회원가입

- [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
    - [x] 회원가입 페이지를 보여줄 때는 GET을 사용한다.
    - [x] 회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
    - [x] 회원가입을 완료하면 index.html로 리다이렉트한다.
- [x] 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.

브라우저에서 HTTP 요청을 다음과 같이 보낸다.

```http request
POST /register HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Content-Length: 80
Content-Type: application/x-www-form-urlencoded
Accept: */*

account = gugu &
password = password &
email = hkkang%40woowahan.com
```

#### 3. Cookie에 JSESSIONID 값 저장하기

로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지한다.
HTTP 서버는 세션을 사용해서 서버에 로그인 여부를 저장한다.

- [x] 세션을 구현하기 전에 먼저 쿠키를 구현해본다.
    - [x] 자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.
    - [x] HTTP 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달한다.
    - [x] 클라이언트 요청 헤더의 Cookie 필드에 값이 추가된다.

서버로부터 쿠키 설정된 클라이언트의 HTTP Request Header 예시

```http request
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
```

Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.

```http request

HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
Content-Length: 5571
Content-Type: text/html;charset=utf-8;
```

#### 4. Session 구현하기

- [x] 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크한다.
- [x] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
- [x] 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.

## 3단계 - 리팩터링

> 앞 단계에서 구현한 코드는 WAS 기능, HTTP 요청/응답 처리, 개발자가 구현할 애플리케이션 기능이 혼재되어 있다.
> 이와 같이 여러 가지 역할을 가지는 코드가 혼재되어 있으면 재사용하기 힘들다.
> 각각의 역할을 분리해 재사용 가능하도록 개선한다.
> 즉, WAS 기능, HTTP 요청/응답 처리 기능은 애플리케이션 개발자가 신경쓰지 않아도 재사용이 가능한 구조가 되도록 한다.

### 기능 요구 사항

#### 1. HttpRequest 클래스 구현하기

- [ ] HTTP 요청을 처리하는 클래스를 추가한다.

HTTP 요청은 어떤 형태로 구성되어 있는가?
클래스로 HTTP 요청을 어떻게 구성하면 좋을까?
HTTP 요청 이미지를 참고해서 구현해보자

#### 2. HttpResponse 클래스 구현하기

- [ ] HTTP 응답을 처리하는 클래스를 추가한다.

HTTP 응답은 어떤 형태로 구성되어 있는가?
클라이언트에게 어떤 형태로 HTTP를 응답하면 좋을까?

#### 3. Controller 인터페이스 추가하기

- [ ] 컨트롤러 인터페이스를 추가하고 각 분기에 있는 로직마다 AbstractController를 상속한 구현체로 만든다.

HTTP 요청, 응답을 다른 객체에게 역할을 맡기고 나니까 uri 경로에 따른 if절 분기 처리가 남는다.
if절 분기는 어떻게 리팩터링하는게 좋을까?
