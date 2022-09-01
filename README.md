# 톰캣 구현하기

- [x] GET 요청시 URI에 대응되는 정적 자원 응답하기
  - [x] `GET /` 요청에 대해 index.html 응답
  - [x] 존재하지 않는 자원을 요청한 경우, 404.html 응답
  - [x] 요청한 자원의 확장자에 따라 Content-Type 설정하여 응답 (text/html, text/css, text/javascript, etc)

- [x] uri에 query string이 포함되면 파싱하여 처리한다.
  - [x] `GET /login.html` 요청시 query string에 대응되는 사용자 데이터가 인메모리 DB에 존재하면 로그에 출력한다.
