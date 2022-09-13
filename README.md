# union
I just think Rust is neat

`Option<T>` and `Result<T,E>` in Java

# What's all this then
This project is an implementation of the [Result-](https://doc.rust-lang.org/stable/std/result/) and [Option API](https://doc.rust-lang.org/stable/std/option/) from Rust, with some omissions because they either have narrow use cases or simply don't make much sense in Java (such as `take`).

# Why
Part learning experience, part experiment. I was curious as to what these types would look like in Java, and whether they are good alternatives to `null` and `throw`, two of my least favorite design-decisions in Java. Furthermore, I wanted to figure out a general design pattern for tagged unions in Java. Much of the inspiration for this come from the answers to [this question on StackOverflow](https://stackoverflow.com/questions/48143268/java-tagged-union-sum-types). 
