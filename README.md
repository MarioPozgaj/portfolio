### How to run

Use `mvn clean install` command to build and run tests, then use `mvn spring-boot:run` to run the app.

There is an Insomnia configuration file `Insomnia_portfolio.json` with the API calls.

There are two users: `user` and `user2` and both use the same password `password`

The app uses basic authentication.

The first request will be the slowest as it's getting a list of all the stock from Alpha Vantage.

### Thought process
I decided to build the backend in Java and Spring as I'm most familiar with this language and framework combination.
I also decided to integrate with the Alpha Vantage API. 

I decided to go with two main endpoints, get all listings and daily time series.
I decided not to go with the search endpoint from Alpha Vantage as I wanted to do my own implementation for filtering and pagination.

I used a ConcurrentHashMap to store the user subscriptions as there is no DB. That should provide thread safety in case multiple users are using the app at the same time.
As for the stock listings I just a regular list as there are no modifications made to that list. It's initialized once and then not modified.
I left it as a regular list rather than an unmodifiable one in case I wanted to make changes to it like stocks getting delisted.

### Compromises made

PostConstruct is not used for getting all the listed stocks due to tests.
PostConstruct is used before the tests are initialized thus it's not possible to mock that call to the AlphaVantage API.

Only filtering and sorting by name and symbol.

I only support the two existing users and have made no system for user creation.

Only implemented the daily time series endpoint.

### Things to implement if there was more time
Database, user registration, simple UI using something like Thymeleaf (seams easy to use with a Spring app).