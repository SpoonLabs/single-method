# single-method

A small library for determining if a patch belongs to a single method.

## Installation

1. Clone the repository.
2. Run the following command to build the jar
   ```bash
   mvn package
   ```
3. Find executable in `target/single-method-*-SNAPSHOT-jar-with-dependencies.jar`

## Usage

It takes in two positional arguments, original and patched version of a source file.

```bash
java -jar target/single-method-*-SNAPSHOT-jar-with-dependencies.jar <original> <patched>
```

It outputs a boolean value, `true` if the patch belongs to a single method, `false` otherwise.

## Example

### `true` case

**left.java**

```java
public class Example {
    public static void main(String[] args) {
        System.out.println("foo");
    }
}
```

**right.java**

```java
public class Example {
    public static void main(String[] args) {
        System.out.println("bar");
    }
}
```

The argument to the `println` method is changed from `"foo"` to `"bar"`. This is a single method change.


### `false` case

**left.java**

```java
public class Example {
    public static void main(String[] args) {
        System.out.println("foo");
    }
}
```

**right.java**

```java
public class Example {
    public static void main(String[] args) {
        System.out.println(getBar());
    }
    
    private static String getBar() {
        return "bar";
    }
}
```

A new method `getBar` is added. This is not a single method change.

