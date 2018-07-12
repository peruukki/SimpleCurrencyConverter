SimpleCurrencyConverter
=======================

Convert between a few currencies and euros, as easily as possible.

See the [GitHub page](http://peruukki.github.io/SimpleCurrencyConverter/) for more details.

## Development setup

The [gradle-retrolambda](https://github.com/evant/gradle-retrolambda) Gradle plugin is used for Java
lambda support. For that, you need to have both JDK 7 and JDK 8 installed, and the `JAVA7_HOME` and
`JAVA8_HOME` environment variables correctly pointing to the JDKs. See
[gradle-retrolambda configuration](https://github.com/evant/gradle-retrolambda#configuration) for
more information.

## Icons

This project uses the `swap horiz` icon from
[Material Design Icons](https://github.com/google/material-design-icons).

## Currency rates

The currency rates are fetched from the
[Free Currency Converter API](https://free.currencyconverterapi.com/).
