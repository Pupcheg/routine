# routine

Generic types for everyday Java code: `Either`, `Pair`, and collectors for them.

[![Maven Central](https://img.shields.io/maven-central/v/me.supcheg/routine)](https://central.sonatype.com/artifact/me.supcheg/routine)
[![Javadoc](https://javadoc.io/badge2/me.supcheg/routine/javadoc.svg)](https://javadoc.io/doc/me.supcheg/routine)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Requires Java 25. Values stored inside the types are guaranteed non-null.
A potentially absent value is expressed explicitly via `Optional` in the type parameter like `Pair<Optional<A>, B>`.

## Installation

```kotlin
implementation("me.supcheg:routine:1.0.0")
```

```xml
<dependency>
    <groupId>me.supcheg</groupId>
    <artifactId>routine</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Either

`Either<L, R>` holds a value in exactly one of two branches.

```java
Either<String, Integer> parse(String s) {
    try {
        return Either.right(Integer.parseInt(s));
    } catch (NumberFormatException e) {
        return Either.left("not a number: " + s);
    }
}

parse("123").mapRight(n -> n * 2);   // Right(246)
parse("x").mapRight(n -> n * 2);     // Left("not a number: x")

// fold both branches into a single value
parse("123").fold(err -> "err: " + err, ok -> "ok: " + ok);   // "ok: 123"

// pattern matching over the sealed type
switch (parse("123")) {
    case Either.Left(var err) -> handleError(err);
    case Either.Right(var n)  -> handleValue(n);
}

parse("123").right();                // Optional[123]
parse("x").ifLeft(System.out::println);   // prints: not a number: x
parse("123").flip();                 // Right(123) -> Left(123)
```

## Pair

`Pair<L, R>` is an immutable record of two non-null components.

```java
Pair<String, Integer> p = Pair.pair("age", 30);

p.left();                  // "age"
p.right();                 // 30
p.mapRight(n -> n + 1);    // ("age", 31)
p.withRight(42);           // ("age", 42)
p.flip();                  // (30, "age")
p.fold((k, v) -> k + "=" + v);   // "age=30"

p.asEntry();               // Map.Entry("age", 30)
Pair.pairFromEntry(entry); // from a Map.Entry
```

## License

MIT
