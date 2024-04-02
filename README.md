# Solution
### 1. Implementation

### 2. Documentation

Chapter contains a description of prepared solution
with additional information HOW TO run&test

### 2.1 Manual testing and remote invocation
curl --data-binary @src/test/resources/trade.csv --header "Content-Type: text/csv" http://localhost:8080/api/v1/enrich

Changes and differences from origin invocation:
- changed a subcommand from 'data' to 'data-binary' motivation: issues with url variable decoding, particularly the new line issues occured.
better to send data in body HTTP section
- changed quotation mark to double quotation mark to proper content type handling