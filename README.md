# HTTP 서버 구현하기

## 1단계 - 요구 사항
- [x] GET /index.html 응답하기
- [x] Query String 파싱
- [x] 로그인에 성공하면 index.html로 리다이렉트
- [x] 알맞은 Http Status Code 응답하기
- [x] POST 방식으로 회원가입하기
- [x] CSS 지원하기

## 리팩토링
- [x] 요청이 들어오면 HTTPRequest의 형태로 만든다.
- [x] Method에 따라 분리한다.
- [x] Body와 QueryString을 분리한다.
- [x] ContentType을 지정한다.
- [x] HttpResponse의 형태로 변환해서 응답한다.

## 2단계 - 리팩토링 요구사항
- [x] RequestLine 클래스를 이용해 HttpRequest 리팩토링
- [x] Add Interface Of Controller

## 3단계 - 요구사항
- [x] Cookie에 JSESSION값 저장하기
- [x] SESSION 구현하기

## 참고 자료
### Http Request
![http request short](https://media.prod.mdn.mozit.cloud/attachments/2016/08/09/13687/5d4c4719f4099d5342a5093bdf4a8843/HTTP_Request.png)
![http request](https://user-images.githubusercontent.com/43840561/131206379-3567c628-8fe5-4e3f-8aca-277594992dbe.png)

### Http Response
![http response short](https://media.prod.mdn.mozit.cloud/attachments/2016/08/09/13691/58390536967466a1a59ba98d06f43433/HTTP_Response.png)
![http response](https://user-images.githubusercontent.com/43840561/131206382-8ed7b3ef-f363-4584-a17f-00ca3728c6b6.png)