## 자바 입출력
- 입출력은 컴퓨터 내부 또는 외부의 장치와 데이터를 주고받는 것을 말한다.
- I/O (Input/Output) 이라고 한다.

## Stream
- 자바에서 입출력을 수행하려면 Stream 이라는 것을 사용하면 된다. 
- 스트림은 연속적인 데이터의 흐름을 물에 비유해서 붙여진 이름이다. 물이 한쪽 방향으로만 흐르는 것 처럼
  스트림은 단방향통신만 가능하기 떄문에 하나의 스트림으로 입력과 출력을 동시에 처리할 수 없다.
- 따라서 입력과 출력을 수행하려면 입력 스트림과 출력 스트림 2개의 스트림이 필요하다.

## 바이트기반 스트림
- 바이트 단위로 데이터를 전송한다.

#### 조상 클래스
- 용도에 따라 많은 바이트 기반 스트림이 존재하는데 모두 InputStream, OutputStream 의 자식들이다.
- 두 클래스는 추상 클래스로 많은 종류의 입출력 장치에서 같은 방식으로 입출력을 제공하기 위한 방법이다.

|InputStream|OutputStream|
|:---:|:---:|
|abstract int read()|abstract void write(int b)|
|int read(byte[] b)|void write(byte[] b)|
|int read(byte[] b, int off, int len)|void write(byte[] b, int off, int len)|
- read() 메서드는 매체로부터 단일 바이트를 읽는데, 0부터 255 사이의 값을 int 타입으로 반환한다.
    - int 값을 byte 타입으로 변환하면 -128부터 127 사이의 값으로 변환된다.
    - 또한 Stream 끝에 도달하면 -1을 반환한다.
  
#### 대표적인 자식 클래스
|입력스트림|출력스트림|용도|
|:---:|:---:|:---:|
|FileInputStream|FileOutputStream|파일|
|ByteArrayInputStream|ByteArrayOutputStream|메모리|

## 보조 스트림
- 스트림의 기능을 보완하기 위해 보조스트림이 제공된다.
- 데이터를 입출력할 수 있는 기능은 없지만, 스트림의 기능을 향상시키거나 새로운 기능을 추가한 스트림
- 대표적으로 버퍼를 사용하여 입출력 성능을 개선하는 BufferedInputStream, BufferedOutputStream 이 있다.
- 아래 처럼 입출력 스트림을 데코레이팅하는 형태로 사용해야 한다. 
```java
BufferedInputStream bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(bytes));
```

#### FilterInputStream, FilterOutputStream
- 두 보조 스트림은 InputStream/OutputStream의 자손이면서 모든 보조스트림의 조상이다.
- 보조스트림은 자체적으로 입출력을 수행할 수 없기 때문에 기반스트림을 필요로 한다.
- FilterInput/OutputStream 은 기반스트림의 메서드를 호출하는 역할을 수행한다.

#### BufferedInputStream, BufferedOutputStream
- 버퍼링을 사용하여 효율적으로 전송이 가능하다.
- flush() 메서드는 버퍼가 아직 가득 차지 않은 상황에서 강제로 버퍼의 내용을 전송한다.
- Stream 은 동기(Synchronous)로 동작하기 떄문에 버퍼가 찰 때까지 기다리면 데드락(deadlock) 상태가 되기 떄문에 flush를 사용한다.

## 스트림 사용 후 close()
- 스트림 사용이 끝나면 close() 메서드를 호출하여 스트림을 닫아줘야 한다.
- 장시간 스트림을 닫지 않으면 파일, 포트 등 다양한 리소스에서 누수(leak)가 발생한다.
- 예외로 ByteArrayInputStream 같이 메모리를 사용하는 스트림은 닫아 주지 않아도 괜찮다.
- 보조 스트림을 사용하는 경우에는 보조 스트림만 close() 하면 기반 스트림이 close() 된다.
- 자바 9 이상에서는 try-with-resources 구문을 이용하면 깔끔하다.

## 문자 기반 스트림
- 자바에서는 한 문자의 단위가 2바이트이기 떄문에 바이트 기반 스트림으로 문자를 처리하는데 어려움이 있다.
- 이를 보완하기 위해 문자 기반의 스트림이 제공된다.
- 이름으로 어떤 스트림인지 구분할 수 있고, byte 대신 char를 사용한다는 것 외에는 바이트 스트림과 유사하게 사용할 수 있다.
```
- InputStream -> Reader
- OutputStream -> Writer
- FileInputStream -> FileReader
- FileOutputStream -> FileWriter
- BufferedInputStream -> BufferedReader
```
- 또한 Reader/Writer와 그 자손들은 인코딩에 관한 처리도 해준다.
- 여러 종류의 인코딩과 자바에서 사용하는 유니코드(UTF-16)간의 변환을 자동적으로 처리해준다.

#### InputStreamReader, OutputStreamReader
- 이름에서 알 수 있듯이 바이트 기반 스트림을 문자 기반 스트림으로 연결시켜주는 역할을 한다.
- 그리고 바이트 기반 스트림의 데이터를 지정된 인코딩의 문자데이터로 변환하는 작업을 수행한다.

#### 예시코드
```java
InputStream inputStream = new ByteArrayInputStream(bytes);
Reader inputStreamReader = new InputStreamReader(inputStream);
Reader bufferedReader = new BufferedReader(reader);

while (bufferedReader.ready()) {
    actual.append(bufferedReader.readLine())
          .append("\r\n");
}
        
bufferedReader.close();
```



