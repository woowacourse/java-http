# 톰캣 구현하기

## 기능 요구 사항

### GET /index.html 응답

인덱스 페이지에 접근할 수 있다.

### CSS 지원

사용자가 페이지를 열었을 때 CSS 파일도 호출할 수 있다.

### Query String 파싱

uri의 quert string을 파싱해서 값을 얻을 수 있다.

### HTTP Status Code 302

- 로그인 여부에 따라 다른 페이지로 이동한다. 
  - 로그인 성공 시: `302` 반환 , `/index.html`로 리다이렉트.
  - 로그인 실패 시: `401.html`로 리다이렉트. 
- 로그인 시 POST 방식을 사용한다.
  - Request Body를 파싱해서 값을 얻는다.

### POST 방식으로 회원가입

- 회원가입 페이지(http://localhost:8080/register)를 접속한다.
  - `register.html`을 보여준다.
  - 페이지를 보여줄 때는 GET 메서드를 사용한다.
- 회원가입 시 POST 방식을 사용한다. 
  - Request Body를 파싱해서 값을 얻는다.
  - 회원가입을 완료하면 `index.html`로 리다이렉트.

### Cookie에 JSESSIONID 값 저장하기

- 쿠키를 구현한다.
  - 응답 헤더에 `Set-Cookie` 추가
  - 세션 아이디를 전달하기 위한 `JSESSIONID` 사용
  - HTTP Request Header의 Cookie에 `JSESSIONID`가 없으면 HTTP Response Header에 `Set-Cookie`를 반환

### Session 구현하기

- 쿠키에서 전달 받은 `JSESSIONID`의 값으로 로그인 여부를 확인한다.
- 로그인 성공 시 Session 객체의 값으로 User 객체를 저장한다.
- 로그인 상태에서 `/login` 페이지에 GET 메서드로 접근하는 경우 `index.html`로 리다렉트.

### 리팩터링

- HttpRequest 구현
- HttpResponse 구현
- Controller Interface 구현
