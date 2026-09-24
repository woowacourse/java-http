# 2단계 로그인 구현하기

## 핵심 요구사항

### HTTP Status Code 302

#### 로그인 성공

로그인에 성공하면 HTTP 상태 코드 `302`와 `Location: /index.html`로 응답한다.

- [x] `POST /login` 본문의 아이디와 비밀번호가 등록된 회원과 일치하면 로그인에 성공한다.

#### 로그인 실패

로그인에 실패하면 HTTP 상태 코드 `302`와 `Location: /401.html`로 응답한다.

- [x] `POST /login` 본문에 등록되지 않은 아이디를 보내면 로그인에 실패한다.
- [x] `POST /login` 본문에 등록된 회원과 다른 비밀번호를 보내면 로그인에 실패한다.
- [x] `GET /login`의 쿼리 문자열로는 로그인하지 않는다.

### POST 방식으로 회원가입

- [x] `GET /register` 요청에 회원가입 페이지를 응답한다.
- [x] `POST /register` 요청에 `Location: /index.html`로 리다이렉트한다.
- [x] `POST /register` 본문의 계정 정보로 회원을 저장해 이후 로그인할 수 있다.
- [x] 로그인 페이지에서 로그인 버튼을 누르면 POST 요청을 보낸다.

### Cookie에 JSESSIONID 값 저장하기

- [x] 요청에 `JSESSIONID` 쿠키가 없으면 빈 세션을 등록하고 그 ID를 `Set-Cookie`로 보낸다.
- [x] 유효한 `JSESSIONID`가 있으면 새 ID를 발급하지 않는다.
- [x] 서버에 없는 `JSESSIONID`가 오면 새 세션을 등록하고 새 ID를 보낸다.

### Session 구현하기

- [x] 로그인에 성공하면 요청에 대응하는 세션에 `User`를 저장하고 같은 ID를 쿠키로 보낸다.
- [x] 로그인한 사용자가 `GET /login`에 접근하면 `/index.html`로 리다이렉트한다.
- [x] 세션이 없거나 세션에 `User`가 없으면 `GET /login`에 로그인 페이지를 응답한다.
