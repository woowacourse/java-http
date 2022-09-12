# 수달 톰캣 구현하기

## 요구사항

### 1. GET /index.html 응답하기

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.

```
GET /index.html HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Accept: */*
실행 결과
Jun-08-2022 15-12-13.gif

```

### 2. CSS 지원하기

- [ ] 접근한 페이지의 js, css 파일을 불러올 수 있다.

```
GET /css/styles.css HTTP/1.1
Host: localhost:8080
Accept: text/css,*/*;q=0.1
Connection: keep-alive
실행 결과
Jun-15-2022 11-45-55.gif
```

3. Query String 파싱

- [ ] uri의 QueryString을 파싱하는 기능이 있다.

```java
실행 결과
        Jun-15-2022 13-19-25.gif


```

## 2단계 요구사항

- [x] HTTP Reponse의 상태 응답 코드를 302로 반환한다.

  로그인 여부에 따라 다른 페이지로 이동시켜보자. /login 페이지에서 아이디는 gugu, 비밀번호는 password를 입력하자. 로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고
  /index.html로 리다이렉트 한다. 로그인에 실패하면 401.html로 리다이렉트한다.

- [x] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
  http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다. 회원가입 페이지를 보여줄 때는 GET을 사용한다. 회원가입을 버튼을 누르면 HTTP
  method를 GET이 아닌 POST를 사용한다. 회원가입을 완료하면 index.html로 리다이렉트한다. 로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.

- [ ] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie 가 존재한다. 로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다. HTTP 서버는 세션을 사용해서 서버에 로그인
  여부를 저장한다. 세션을 구현하기 전에 먼저 쿠키를 구현해본다.

자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.

서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달하면 클라이언트 요청 헤더의
Cookie 필드에 값이 추가된다.

- [ ] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다. 쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다. 로그인에 성공하면
  Session 객체의 값으로 User 객체를 저장해보자.

그리고 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.

## 로그인 플로우 정리

1. 최초 로그인 페이지 로딩 , 데이터 , 쿠키도 없음

2. 로그인 페이지에서 데이터 없고 쿠키 있음
    1. 쿠키가 있는데, 유효하지 않음

4.로그인 페이지에서 데이터 입력 했음 (성공)

3. 로그인 페이지에서 데이터 입력 했음 (실패)

## 리팩터링 내용 정리

- [ ] controller Mapping 로직 개선
- [ ] controller 내부에 서블릿 동작 원리와 유사하게 doGet, doPost 적용
- [ ] custom controller 에 적절하게 doGet, doPost 오버라이드 해서 동작 정리
- [ ] response create 로직 개편
- [ ] 적절한 예외 처리에 대해 고민해보기
- [ ] 클래스, 패키지간 의존 관계 고민해보기
- [ ] 공식 문서에서 작성한 메서드명, 클래스 명 보고 좋은 네이밍에 대해서 고민해보기
- [ ] 클래스 접근 제어자 권한에 대한 고민해보기
- [ ] 테스트 로직 작성하면서 의도한대로 동작하는지 확인해보기 
