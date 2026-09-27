# 1단계 HTTP 서버 구현하기

## 인수 조건

### 루트 요청

- [x] `GET /` 요청에 `Hello world!`를 응답한다.

### 정적 HTML

- [x] `GET /index.html` 요청에 `static/index.html`의 내용을 응답한다.

### CSS

- [x] CSS 리소스 요청에 해당 파일 내용과 `text/css;charset=utf-8` Content-Type을 응답한다.

### Query String

- [x] Query String이 포함된 `/login` 요청을 `/login` 경로 요청으로 처리해 로그인 페이지를 응답한다.
