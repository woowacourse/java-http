# 톰캣 구현하기

## 공식 문서 및 참고 사이트

- [RFC 2616](https://www.rfc-editor.org/rfc/rfc2616)
- [MDN HTTP](https://developer.mozilla.org/en-US/docs/Web/HTTP)

## 1단계 요구사항

- [x] GET /index.html 응답하기
```markdown
인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있도록 만들자.
Http11ProcessorTest 테스트 클래스의 모든 테스트를 통과하면 된다.
브라우저에서 요청한 HTTP Request Header는 다음과 같다.
```
- [x] CSS 지원하기
```markdown
인덱스 페이지에 접속하니까 화면이 이상하게 보인다.
개발자 도구를 열어서 에러 메시지를 체크해보니 브라우저가 CSS를 못 찾고 있다.
사용자가 페이지를 열었을 때 CSS 파일도 호출하도록 기능을 추가하자.
```
- [x] Query String 파싱
```markdown
http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)를 보여주도록 만들자.
그리고 로그인 페이지에 접속했을 때 Query String을 파싱해서 아이디, 비밀번호가 일치하면 회원을 조회한 결과가 나오도록 만들자.
```

## 2단계 요구사항

- [ ] HTTP Reponse의 상태 응답 코드를 302로 반환한다.
```markdown
로그인 여부에 따라 다른 페이지로 이동시켜보자.
`/login` 페이지에서 아이디는 gugu, 비밀번호는 password를 입력하자.
로그인에 성공하면 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트 한다.
로그인에 실패하면 `401.html`로 리다이렉트한다.
```
- [ ] POST로 들어온 요청의 Request Body를 파싱할 수 있다.
```markdown
http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.
회원가입 페이지를 보여줄 때는 GET을 사용한다.
회원가입을 버튼을 누르면 HTTP method를 GET이 아닌 POST를 사용한다.
회원가입을 완료하면 `index.html`로 리다이렉트한다.
로그인 페이지도 버튼을 눌렀을 때 GET 방식에서 POST 방식으로 전송하도록 변경하자.
```
- [ ] 로그인에 성공하면 HTTP Reponse의 헤더에 Set-Cookie가 존재한다.
```markdown
로그인에 성공하면 쿠키와 세션을 활용해서 로그인 상태를 유지해야 한다.
HTTP 서버는 세션을 사용해서 서버에 로그인 여부를 저장한다.
세션을 구현하기 전에 먼저 쿠키를 구현해본다.

자바 진영에서 세션 아이디를 전달하는 이름으로 JSESSIONID를 사용한다.

서버에서 HTTP 응답을 전달할 때 응답 헤더에 Set-Cookie를 추가하고 JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 형태로 값을 전달하면 클라이언트 요청 헤더의 Cookie 필드에 값이 추가된다.
```
- [ ] 서버에 세션을 관리하는 클래스가 있고, 쿠키로부터 전달 받은 JSESSIONID 값이 저장된다.
```markdown
쿠키에서 전달 받은 JSESSIONID의 값으로 로그인 여부를 체크할 수 있어야 한다.
로그인에 성공하면 HttpSession 객체의 값으로 User 객체를 저장해보자.

그리고 로그인된 상태에서 /login 페이지에 HTTP GET method로 접근하면 이미 로그인한 상태니 index.html 페이지로 리다이렉트 처리한다.
```
