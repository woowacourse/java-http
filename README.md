# 톰캣 구현하기

## 구현 목록

### 1단계
- [x] "/" 으로 GET 요청이 오면 "Hello World!" 문자열을 반환하도록 한다.
- [x] "/index.html" 으로 GET 요청이 오면 index.html 파일을 반환하도록 한다.
- [x] CSS 파일에 대한 GET 요청이 오면 css 파일을 반환하도록 한다.
- [x] "/login?account={계정명}&password={비밀번호}" 로 GET 요청이 오면 login.html 파일을 반환하도록 한다.
  - [x] 이 때 Query String 을 파싱하여 아이디, 비밀번호가 일치한다면 콘솔창에 로그로 회원 조회 결과를 출력한다.

### 2단계
- [ ] "/login" 페이지의 입력값에 따라 서로 다른 페이지를 반환한다.
  - [ ] 로그인에 성공하면 302 상태코드를 반환하고 "/index.html" 로 **리다이렉트**한다.
  - [ ] 로그인에 실패하면 "/401.html" 을 반환한다.
- [ ] GET "/register" 요청을 보내면 "register.html" 페이지를 반환한다.
  - [ ] POST "/register" 요청을 보내면 회원가입 진행 후, "/index.html" 로 리다이렉트한다.
- [ ] `Set-Cookie` 를 통해 로그인에 성공한 사용자의 세션 ID (`JSESSIONID`) 를 쿠키에 담아 전달한다.
  - [ ] Cookie 클래스를 추가한다.
  - [ ] Request 헤더에 `JSESSIONID` 가 없으면 Response 헤더에 `Set-Cookie` 를 설정한다.
- [ ] 로그인에 성공한 사용자를 세션에 저장한다.
  - [ ] 로그인 된 상태로 GET "/login" 에 접속하면 "/index.html" 로 리다이렉트한다.
