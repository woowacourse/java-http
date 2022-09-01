# 톰캣 구현하기

## 🚀 1단계 - HTTP 서버 구현하기
### 1. GET /index.html 응답하기

- processor안에 전달받은 socket에서 url 추출하기 `/index.html`  ✅
- 추출한 url로 `static/index.html` 에 접근하기 ✅
- `index.html` 파일의 내용을 response body 값에 담아서 반환하기 ✅

### 2. CSS, javscript 지원하기

- 정적 파일을 조회한다. ✅
  - 정적 페이지가 없을 땐 404.html을 반환한다.
- 파일의 확장자에 맞게 response header의 `Accept`  값을 넣어준다. ✅

### 3. Query String 파싱

- `GET /login` 으로 요청이 왔을 때 login.html 을 응답한다. ✅
- Query String을 파싱한다. ✅
  - Query String 없이 요청이 올 경우 `login.html` 만을 응답한다.
- 파싱된 아이디, 비밀번호와 일치하는 회원을 조회하고 로그로 확인해본다. ✅
  - 올바르지 않은 회원인 경우 `401.html`을 응답한다.
