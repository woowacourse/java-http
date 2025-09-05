## 톰캣 구현하기

### 1. HTTP 서버 구현하기

- [x] 인덱스 페이지(html + css)에 접근할 수 있다.
   - http://localhost:8080/index.html
- [x] 로그인 페이지(html)을 응답할 수 있다.
   - http://localhost:8080/login?account=gugu&password=password
- [x] 쿼리 파라미터의 정보로 회원을 조회할 수 있다.
   - 콘솔 로깅 확인
   - 존재하지 않는 회원에 대해서 검증 로깅 확인 가능
- [x] 존재하지 않는 페이지에 대해서 요청할 시 notFound 응답
  - http://localhost:8080/ajfdlksjaf
  - F12를 눌러서 확인하면 404 상태 반환