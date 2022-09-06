# 🐱 톰캣 구현하기 2단계 - 로그인 구현하기

## 🚀 미션 설명

서블릿을 도입해서 동적 페이지를 만들 수 있게 되었다.

이제 로그인과 회원가입 기능을 추가해보자.

로그인에 필요한 쿠키와 세션도 같이 구현해보자.

## ⚙️ 기능 요구 사항

### 1. HTTP Status Code 302

로그인 여부에 따라 다른 페이지로 이동시켜보자.

/login 페이지에서 아이디는 gugu, 비밀번호는 password를 입력하자.

로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 /index.html로 리다이렉트 한다.
로그인에 실패하면 401.html로 리다이렉트한다.

### 2. POST 방식으로 회원가입

http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.

회원가입 페이지를 보여줄 때는 GET을 사용한다.

회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.

회원가입을 완료하면 index.html로 리다이렉트한다.

로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.

### 3. Cookie에 JSESSIONID 값 저장하기

로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.

HTTP 서버는 세션을 사용해서 서버에 로그인 여부를 저장한다.
세션을 구현하기 전에 먼저 쿠키를 구현해본다.

자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.

서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달하면 클라이언트 요청 헤더의 Cookie 필드에 값이 추가된다.

서버로부터 쿠키 설정된 클라이언트의 HTTP Request Header 예시

```text
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
```

Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.

```text
HTTP/1.1 200 OK 
Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46
Content-Length: 5571
Content-Type: text/html;charset=utf-8;
```

### 4. Session 구현하기

쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
로그인에 성공하면 Session 객체의 값으로 User 객체를 저장해보자.

그리고 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.



## 🖊 체크리스트

- [ ] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
- [ ] POST 로 들어온 요청의 Request Body를 파싱할 수 있다.
- [ ] 로그인에  성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
- [ ] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.

## 🖥 기능 목록

- [ ] HTTP Status Code 302
- [ ] POST 방식으로 회원가입
- [ ] Cookie 에 JSESSIONID 값 저장하기
- [ ] Session 구현하기

## 🔥 리팩토링 목록

### ⚽️ ResponseEntity 실제 구조처럼 만들기

- [ ] builder 패턴 사용해 status Code 별 분리하기
- [ ] 각 상태코드 별 필요 헤더 추가할 수 있게 세팅하기
- [ ] 바디 값을 따로 넣을 수 있게 세팅하기

### 📝 피드백

#### 1차 피드백

