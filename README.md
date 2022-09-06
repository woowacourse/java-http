# 톰캣 구현하기

## 요구사항

### [🚀 1단계 - HTTP 서버 구현하기](https://techcourse.woowahan.com/s/cCM7rQR9/ls/QX02zDYN)
- [X] `GET /index.html` 요청에 대해 index 페이지와 상태코드 200을 응답한다.
    - [X] 요청으로부터 요청 uri를 가져온다.
    - [X] url에 따라 적절한 resource를 응답한다.
- [X] `ContentType` header값의 내용을 적절하게 응답한다.
    - [X] 요청 헤더의 `Accept` 값을 가져온다.
    - [X] 응답 헤더에서 알맞은 값을 응답한다.
- [X] `GET /login?account=gugu&password=password` 요청에 대해 query string을 parsing하여 회원을 조회한다.
    - [X] 해당 요청에 대해 `login.html` 페이지를 응답한다.
    - [X] 요청 uri로부터 query parameter를 추출한다.
    - [X] account와 password를 확인하여 유저 정보를 조회한 후 콘솔에 로깅한다.

### [🚀 2단계 - 로그인 구현하기](https://techcourse.woowahan.com/s/cCM7rQR9/ls/YSC17uAy)
- [X] 로그인 성공 시 `index.html`로 redirect, 실패 시 `401.html`로 redirect한다.
    - [X] 로그인에 성공하면 `Status Code`를 `302 Found`로 응답하고 `Location` header를 추가한다.
    - [X] 로그인에 실패하면 `Status Code`를 `303 See Other`로 응답하고 `Location` header를 추가한다.
- [X] `POST /register` 요청을 받아 회원 가입을 처리한다.
    - [X] `GET /register`요청 시 `register.html` 페이지를 응답한다.
    - [X] `POST` 요청의 request body에서 데이터를 읽어온다.
    - [X] 회원 가입이 완료되면 `index.html`로 redirect 한다.
- [X] 로그인 처리를 `POST` 요청을 받아 진행한다.
    - [X] `GET /login` 요청 시 `login.html` 페이지를 응답한다.
    - [X] `POST /login` 요청 시 request body를 읽어 로그인을 처리한다.
- [X] 로그인 성공 시 쿠키에 `JSESSIONID`를 저장한다.
    - [X] 로그인 성공 시 랜덤한 UUID를 생성한다.
    - [X] 생성한 UUID를 `Set-Cookie` 헤더의 `JSESSIONID` 값으로 응답한다.
- [X] 로그인한 상태에서 `GET /login` 요청을 보내면 `index.html`로 redirect한다.
    - [X] 로그인 성공 시 세션을 생성해 `user`를 저장한다.
    - [X] 생성한 세션의 ID를 쿠키에 저장한다.
    - [X] `GET /login` 요청 시 쿠키에 세션 ID가 있는지 확인한다.
    - [X] 해당 세션에 `user`가 저장되어 있으면 `index.html`로 redirect 한다.

### [🚀 3단계 - 리팩터링](https://techcourse.woowahan.com/s/cCM7rQR9/ls/lRDyKWbV)
- [X] `HttpRequest` 클래스 구현
- [X] `HttpResponse` 클래스 구현
- [ ] `Controller`, `AbstractController`, `RequestMapping` 클래스 구현을 통한 요청 분기 처리
