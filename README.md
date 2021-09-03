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
- ViewResolver에서의 에러 처리를 진행한다고 하자. 이게 서버 내부적인 로직의 문제이기 때문에, 500 response를 보내줘야 하나?(나의 View는 사용자에게 직접 노출되지 않는다.)
- "text/html" 과 같은 파일 형식을 직접 file로 부터 얻어왔었는데, 이는 기존의 테스트가 깨지는 현상 ("text/html;charset=utf-8" 필요)발생. ContentType이라는 클래스를 별도로 분리하여 관리할지 고민
- ErrorPage로의 Redirection로직을 HttpStatus.Found를 통해 우선적으로 Browser가 Redirect요청을 보내도록 구성했다. js로 이를 별도로 처리하거나, Response 자체를 HttpStatus.ok 에 파일을 보내줄 수도 있을듯 하다.
