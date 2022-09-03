# 톰캣 구현하기

## 기능 요구 사항

### 1단계

- [x] http://localhost:8080/index.html 페이지에 접근 가능하다.
- [x] 접근한 페이지의 js, css 파일을 불러올 수 있다.
- [x] uri의 QueryString을 파싱하는 기능이 있다.

### 2단계

- [ ] 로그인 여부에 따라 페이지를 이동한다
    - [ ] `/login`페이지에서 로그인에 성공하면 응답 헤더에 `http status code를 302`로 반환하고 `/index.html로 리다이렉트` 한다.
    - [ ] 로그인에 실패하면 `401.html`로 리다이렉트한다.
- [ ] POST방식으로 회원가입한다.
    - [ ] 회원가입 페이지를 보여줄 때는 `GET`을 사용한다.
    - [ ] `POST`로 회원가입을 완료하면 `index.html`로 리다이렉트한다.
    - [ ] 로그인 페이지 버튼을 눌렀을 때 `GET`방식에서 `POST` 방식으로 전송하도록 변경한다.
- [ ] Cookie 클래스를 추가하고 HTTP Request Header의 Cookie에 JSESSIONID가 없으면 HTTP Response Header에 Set-Cookie를 반환해주는 기능을 구현한다.
- [ ] 로그인에 성공하면 Session 객체의 값으로 User 객체를 저장한다.
- [ ] 로그인된 상태에서 `/login` 페이지에 HTTP `GET` method로 접근하면 이미 로그인한 상태니 `index.html 페이지로 리다이렉트` 처리한다.
