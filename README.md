# 톰캣 구현하기

- [x] GET 요청시 URI에 대응되는 정적 자원 응답하기
  - [x] `GET /` 요청에 대해서도 index.html 응답하기
  - [ ] 존재하지 않는 자원을 요청한 경우, 404 예외 응답하기
  - [x] 요청한 자원의 확장자에 따라 Content-Type 설정하여 응답하기 (text/html, text/css, text/javascript, etc)
