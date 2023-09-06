# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기

- [x] GET /index.html에 대해 응답하도록 한다.
    - [x] 요청의 첫 라인을 읽어오도록 파싱한다.
        - [x] request uri를 추출한다.
        - [x] 요청 url에 해당되는 파일을 resource 디렉토리에서 읽게 한다.
    - [x] 요청의 나머지 헤더를 읽어오도록 파싱한다.
    - [x] line이 null인 경우 예외처리를 한다.
    - [x] 존재하지 않는 페이지를 요청할 경우 404.html을 반환한다.
- [x] 사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 한다.
    - [x] 요청 파일의 확장자가 css, 또는 요청 헤더의 Accept가 text/css인 경우 응답 헤더의 Content-Type을 text/css로 전송한다.
- [x] Query String을 파싱하게 한다.
    - [x] request의 첫 라인에서 추출한 uri에서 쿼리 데이터를 추출한다.
    - [x] InMemoryUserRepository를 사용해 미리 가입되어 있는 회원을 조회하고 로그로 출력한다.

## 2단계 - 로그인 구현하기

- [x] 로그인 여부에 따라 다른 페이지로 이동시킨다.
    - [x] /login 페이지에서 로그인에 성공하면 http status code를 302로 반환하고 /index.html로 리다이렉트한다.
    - [x] /login 페이지에서 로그인에 실패하면 401.html로 리다이렉트한다.
- [x] POST 방식으로 회원가입
    - [x] `http://localhost:8080/register` 으로 접속하면 회원가입 페이지(register.html)를 보여준다.
    - [x] POST 요청에 대해 별도의 처리를 하도록 로직을 분리한다.
    - [x] 회원가입이 완료되면 index.html로 리다이렉트한다.
    - [x] 로그인 페이지에서 버튼을 눌렀을 때 POST 방식으로 전송하도록 변경한다.
- [x] Cookie에 JSESSIONID 값을 저장한다.
    - [x] 세션 아이디를 전달하는 이름으로 `JSESSIONID`를 사용한다.
    - [x] 서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하여 값을 전달한다.
    - [x] Cookie 클래스 구현
    - [x] 요청 헤더의 Cookie에 JSESSIONID가 없으면 응답 헤더에 Set-Cookie를 반환하게 한다.
- [x] Session 구현
    - [x] 쿠키에서 전달받은 JSESIONID의 값으로 로그인 여부를 체크한다.
    - [x] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
    - [x] 로그인된 상태에서 /login 페이지에 GET method로 접근하면 index.html 페이지로 리다이렉트한다.

## 3단계 - 리팩토링

- [x] Request, Response 객체 분리
- [x] Http11Processor의 메서드 분리
- [ ] Controller 분리

## 4단계 - 동시성 확장하기