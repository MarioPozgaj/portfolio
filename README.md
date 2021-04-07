### How to run

Run `mvn clean install`, then run `mvn spring-boot:run`

### Compromises made

PostConstruct is not used for getting all the listed stocks due to tests.
PostConstruct is used before the tests are initialized thus it's not possible to mock that call to the AlphaVantage API.

Only filtering and sorting by name and symbol.