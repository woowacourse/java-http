# HTTP 서버 구현하기

## 1단계 - HTTP 서버 구현하기

1. GET /index.html 응답하기
2. Query String 파싱
3. HTTP Status Code 302
4. POST 방식으로 회원가입
5. CSS 지원하기

## 구현해야할 기능
- GET 요청 시 Query-String Request 받기
- 각 요청 별 테스트 코드 작성하기


## ISSUE
- ControllerAdvice 의 에러처리는 그 Map에 넣어둔 순서에 따라 영향을 받음.(Exception을 가장 먼저 넣게 되면 모든 Exception의 처리는 그 Map에 정의된 방법으로 진행)
-> 현재 기능 구현을 LinkedHashMap으로 진행했지만, 실제로 ExceptionHandler에서는 어떻게 처리하는지 알아보고 이를 적용해보기(깊이가 가장 가까운 ExceptionHandling이 되는 것으로 알고 있음.)
