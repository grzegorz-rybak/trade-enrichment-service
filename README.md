# Solution
### 1. Contents

- How to run the service.
- How to use the API.
- Any limitations of the code.
- Any discussion/comment on the design.
- Any ideas for improvement if there were more time available.

### 2. Documentation

Chapter contains a description of prepared solution
with additional information HOW TO run&test

### 2.1 How to run the service

Manual testing and remote invocation

___ SpringBoot based application.: 
1. BUILD
    mvn clean install
2. RUN
    java -jar trade-enrichment-service-0.0.1-SNAPSHOT.jar

3. Test with curl
   curl --data-binary @src/test/resources/trade.csv --header "Content-Type: text/csv" http://localhost:8080/api/v1/enrich

Changes and differences from origin invocation:
- changed a subcommand from 'data' to 'data-binary' motivation: issues with url variable decoding, particularly the new line issues occured.
better to send data in body HTTP section
- changed quotation mark to double quotation mark to proper content type handling

### 2.2 How to use the API.

Post HTTP command was prepared. Data should be sent as body request.
Additional MockMVC tests were prepared to verify behaviour.

___ SpringBoot based application. Default port: 8080
___ SpringBoot based application.: path: api/v1/enrich 

Additional info:
 - Application parameters with default values of:
server.tomcat.threads.max=20
server.connection-timeout=5s
server.tomcat.max-http-post-size=300MB

### 2.3 Any limitations of the code.
Time consumption for i/o operation is high.
Memory requirements is high due to product set management.
Implementation opened for extensions closed for changes.
Response is sent after data is prepared. (for example the SseEmitter might be used to increase performance)

### 2.4 Any discussion/comment on the design.

Solution was prepared with TDD manner. First of all verification test
suit was implemented. Before complete implementation tests failed.
After a proper solution was gain the tests "appear green". This allowed
to keep consistency and avoid regression while preparing solution.

Architecture based on Onion style, as well as adapting DDD approach.
External services prepared outside core implementation connected only via interfaces.
ex. TimeStampValidator or ProductService interface. The functionality of external libraries were injected.
Application behaviour may be changed with Qualifier annotation for 
other services implementations.

As output response creator the StreamingResponseBody was used in order
to provide stream based processing.

### 2.5 Any ideas for improvement if there were more time available.

Efficiency: 2,5 mln records are processed in 7seconds.
(Intel Core i7-10750H; 32GB RAM)

Additional Thread management might be applied.
Input file read phase should be changed to allow stream processing.
Products map should be changed and supply with cache or randomAccessFile approach.
Thanks to product set structure the access time complexity might be shrinked to O(N)
using lookup table with strict indexing.

The amount of the logs is high even not existing product occurs multiple times.