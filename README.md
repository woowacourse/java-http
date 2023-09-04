# 톰캣 구현하기

## HTTP 서버 구현하기
### html 파일 응답하기
* [x] HTTP Request Header에서 필요한 정보를 파싱한다.
  * [x] HTTP 메소드를 저장한다.
  * [x] Request URI(html 파일)를 저장한다.
  * [x] HTTP 버전을 저장한다.
* [x] Request URI에 해당하는 파일을 responseBody로 돌려준다.

### CSS 지원하기
* [ ] 요청 리소스가 CSS or JS인 경우 Response Header에 적절한 Content-Type을 보낸다. 

### Query String 파싱
* [ ] Request URI에서 QueryString을 파싱해 저장한다.
