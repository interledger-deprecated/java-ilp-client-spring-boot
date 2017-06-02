# java-ilp-client-spring-boot
A command line client runner using Spring Boot

## Develop

### Dependencies

The project is setup to find project dependencies in the same directory so the easiest way to work on the code is to fetch the dependencies as-is from GitHub.

```bash

    $ git clone https://github.com/interledger/java-crypto-conditions.git
    $ git clone https://github.com/interledger/java-ilp-core.git
    $ git clone https://github.com/interledger/java-ilp-client.git
    $ git clone https://github.com/interledger/java-ilp-ledger-adaptor-rest-spring.git

```

### Gradle/Maven

The project supports both Gradle and Maven build tools. A special Gradle task is defined which will generate a POM file for Maven.

```bash

    $ gradle writePom

```

### CheckStyles

The project uses Checkstyle for consitency in code style. We use the Google defined Java rules which can be configured for common IDE's by downloading configuration files from the [GitHub repo](https://github.com/google/styleguide).

## Configuration

The application will read configuration from application.properties. For development/testing override these properties in a file at config/application.properties

## Contributors

Any contribution is very much appreciated! [![gitter][gitter-image]][gitter-url]

## License

This code is released under the Apache 2.0 License. Please see [LICENSE](LICENSE) for the full text.
