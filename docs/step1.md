# 1단계 HTTP 서버 구현하기

## 핵심 요구사항

### 정상 동작 확인

메인 클래스로 HTTP 서버를 실행하고, 웹브라우저에서 `http://localhost:8080`에 접속하면 `Hello world!`가 보인다.

- [x] `/` 요청에 `Hello world!` 응답을 반환한다.

### GET /index.html 응답하기

`GET /index.html` 요청에 `static/index.html`의 내용으로 응답한다.

- [x] `/index.html` 요청에 인덱스 페이지를 반환한다.

### CSS 지원하기

CSS 리소스 요청에 해당 파일의 내용과 CSS `Content-Type`으로 응답한다.

- [x] CSS 리소스 요청에 해당 파일의 내용으로 응답한다.
- [x] CSS 리소스 요청에 `Content-Type: text/css;charset=utf-8`로 응답한다.

### Query String 로그인

`/login?account=gugu&password=password` 요청의 경로와 Query String을 구분하고, 전달된 계정 정보로 회원을 조회한다.

- [x] Query String이 있는 로그인 요청에 로그인 페이지를 반환한다.
- [ ] 전달된 계정 정보와 일치하는 회원 조회 결과를 로그로 남긴다.

