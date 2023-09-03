## Mission 1 : HTTP 서버 구현하기

1. [x] Get /index.html 응답하기
2. [x] CSS 지원하기
   - [x] 정적 파일을 지원하는 ResourceProvider 를 생성한다.
      - [x] 해당 정적 파일을 읽어온다.
      - [x] 해당 정적 파일의 타입을 반환한다.
3. [x] Query String 파싱
   - [x] 먼저 정적 파일을 찾는다.
   - [x] 요청을 handle 할 수 있는 핸들러를 찾는다.
   - [x] 아무것도 못 찾았다면 not Found 페이지 반환.
