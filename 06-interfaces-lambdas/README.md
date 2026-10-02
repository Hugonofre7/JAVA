# Chapter 6 — Interfaces & Lambdas

📁 **Carpeta:** `06-interfaces-lambdas/`
📄 **Archivo:** `InterfacesAndLambdas.java`
🎯 **Retos totales del capítulo:** 3

---

## 🎯 Objetivo del capítulo

Este capítulo estudia conceptos fundamentales de la programación funcional de Java relacionados con:

* Interfaces funcionales
* Lambda expressions
* Closures
* Variables capturadas
* `effectively final`
* `Predicate`
* Method References
* Composición funcional
* `invokedynamic`
* Bootstrap Methods
* `LambdaMetafactory`
* Call Sites
* Cacheo de Call Sites

Los retos combinan el uso de interfaces funcionales y expresiones lambda con una perspectiva orientada a JVM, mostrando cómo una construcción de alto nivel como una lambda puede relacionarse con mecanismos internos como `invokedynamic`, bootstrap methods y call sites.

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals, 12th Edition* — Cay S. Horstmann

El capítulo se complementa con conceptos de la JVM relacionados con la implementación de expresiones lambda, `invokedynamic`, bootstrap methods y `LambdaMetafactory`.

---

# 🧪 Reto 1 — Interfaz funcional propia + lambda con closure

## 🎯 Objetivo

Comprender cómo una interfaz funcional puede ser implementada mediante una expresión lambda y cómo una lambda puede capturar una variable definida en el contexto donde es creada.

Se utiliza una política de reintentos representada mediante una interfaz funcional:

```java
@FunctionalInterface
interface RetryPolicy {
    boolean shouldRetry(int attemptNumber);
}
```

La lambda captura la variable `maxAttempts`:

```java
int maxAttempts = 3;

RetryPolicy policy = attempt -> attempt < maxAttempts;
```

Posteriormente se utiliza la interfaz para determinar si una operación debe volver a intentarse:

```java
for (int i = 0; i < 6; i++) {
    System.out.println(
        "Attempt " + i + " -> retry: " + policy.shouldRetry(i)
    );
}
```

---

# 🔮 Pregunta de predicción

> ¿Qué ocurre si intentamos modificar `maxAttempts` después de crear la lambda?

### Predicción

La modificación provocará un error de compilación porque una variable local capturada por una lambda debe ser `final` o **effectively final**.

La regla garantiza que el valor capturado permanezca estable después de que la lambda haya sido creada.

---

## 🧠 Closure y variables capturadas

Una lambda puede utilizar variables definidas fuera de su propio cuerpo.

En este caso:

```text
                    Stack
                      │
                      ▼
              maxAttempts = 3
                      │
                      │ captura
                      ▼
              ┌───────────────┐
              │     lambda    │
              │ attempt < 3   │
              └───────────────┘
                      │
                      ▼
                RetryPolicy
```

La lambda mantiene acceso al valor capturado aunque `maxAttempts` no sea un parámetro de la lambda.

Java evita que una variable local capturada pueda cambiar posteriormente.

Por eso esto es válido:

```java
int maxAttempts = 3;

RetryPolicy policy = attempt -> attempt < maxAttempts;
```

Mientras que esto no:

```java
int maxAttempts = 3;

RetryPolicy policy = attempt -> attempt < maxAttempts;

maxAttempts = 5;
```

---

## 🧠 `effectively final`

Una variable es **effectively final** cuando no se modifica después de su inicialización.

No es necesario escribir explícitamente:

```java
final int maxAttempts = 3;
```

Si nunca se modifica, Java la considera effectively final.

Esto permite que pueda ser capturada por una lambda.

Desde una perspectiva de diseño, esta restricción evita que una lambda dependa de un estado local mutable cuyo valor podría cambiar después de su creación.

---

# 🧠 Conceptos consolidados — Reto 1

* Interfaz funcional
* `@FunctionalInterface`
* Lambda expression
* Closure
* Variable capturada
* `effectively final`
* Contexto léxico
* Inmutabilidad de variables capturadas

---

# 🧪 Reto 2 — Referencias a Métodos y composición funcional

## 🎯 Objetivo

Comprender cómo una referencia a método puede representar una implementación funcional existente y cómo las interfaces funcionales estándar del JDK permiten construir operaciones mediante composición.

Primero se define un método estático:

```java
static boolean isEven(int n) {
    return n % 2 == 0;
}
```

Después se representan la misma operación mediante una lambda y mediante una referencia a método:

```java
Predicate<Integer> lambdaVersion =
        n -> n % 2 == 0;

Predicate<Integer> methodRefVersion =
        InterfacesAndLambdas::isEven;
```

Ambas representaciones producen el mismo resultado:

```java
lambdaVersion.test(4);
methodRefVersion.test(4);
```

---

# 🔮 Pregunta de predicción

> ¿Qué ventaja proporciona una referencia a método cuando ya existe un método cuya firma coincide con la interfaz funcional?

### Predicción

La referencia a método permite expresar directamente que una interfaz funcional utilizará un método existente, evitando crear una lambda que simplemente delega la llamada.

Por ejemplo:

```java
n -> isEven(n)
```

puede expresarse como:

```java
InterfacesAndLambdas::isEven
```

La segunda forma comunica de manera más directa la intención.

---

## 🧠 Lambda vs Method Reference

Conceptualmente:

```text
Lambda
   │
   │ n -> isEven(n)
   ▼
┌─────────────────┐
│ llamada a método│
└─────────────────┘
         │
         ▼
      isEven(n)
```

Con una referencia a método:

```text
Method Reference
       │
       │ ::isEven
       ▼
┌─────────────────┐
│ método existente│
└─────────────────┘
```

Ambas formas representan comportamiento compatible con una interfaz funcional.

La diferencia principal está en la forma de expresar la intención.

---

## 🧠 Composición funcional

Las interfaces funcionales del JDK proporcionan métodos que permiten combinar operaciones.

Por ejemplo:

```java
Predicate<Integer> greaterThanTen =
        n -> n > 10;

Predicate<Integer> evenAndGreaterThanTen =
        methodRefVersion.and(greaterThanTen);
```

El resultado representa conceptualmente:

```text
        número
           │
           ▼
     ┌───────────┐
     │   isEven  │
     └─────┬─────┘
           │ true
           ▼
     ┌───────────────┐
     │ greaterThan10 │
     └───────┬───────┘
             │
             ▼
           result
```

La composición permite construir comportamiento más complejo a partir de operaciones pequeñas.

---

## 🧠 `invokedynamic`

Aunque una lambda y una referencia a método parecen construcciones sencillas del lenguaje, su implementación se relaciona con `invokedynamic`.

Conceptualmente:

```text
Java source
     │
     ▼
Lambda / Method Reference
     │
     ▼
Bytecode
     │
     ▼
invokedynamic
     │
     ▼
Bootstrap Method
     │
     ▼
LambdaMetafactory
     │
     ▼
Call Site
     │
     ▼
Implementación funcional
```

Esto permite que la JVM tenga flexibilidad para determinar cómo materializar el comportamiento durante la ejecución.

---

# 🧠 Conceptos consolidados — Reto 2

* `Predicate<T>`
* Method References
* `::`
* Composición funcional
* `Predicate.and()`
* Lambda expressions
* `invokedynamic`
* Call Sites
* Bootstrap Methods

---

# 🧪 Reto 3 — `invokedynamic` en la práctica: generación de lambdas en frío

## 🎯 Objetivo

Observar experimentalmente el comportamiento de la JVM al crear y ejecutar lambdas repetidamente desde el mismo call site.

Se construye un benchmark utilizando:

```java
static void benchmarkLambdaCreation(int n) {
    long startTime = System.nanoTime();

    for (int i = 0; i < n; i++) {
        Runnable r = () -> {};
        r.run();
    }

    long endTime = System.nanoTime();

    long elapsed = endTime - startTime;

    System.out.println("Iterations: " + n);
    System.out.println("Total time: " + elapsed + " ns");

    if (n > 0) {
        System.out.println(
            "Average: " + (elapsed / (double) n) + " ns"
        );
    }
}
```

El benchmark se ejecuta inicialmente con:

```java
benchmarkLambdaCreation(1);
```

y posteriormente con:

```java
benchmarkLambdaCreation(1_000_000);
```

---

# 🔮 Pregunta de predicción

> Si se ejecuta el mismo call site de una lambda un millón de veces, ¿el costo de creación crecerá de manera perfectamente lineal?

### Predicción

No necesariamente.

La expectativa es que el costo no crezca de manera perfectamente lineal debido al funcionamiento de la JVM y al cacheo asociado al call site.

La primera ejecución puede incluir trabajo adicional relacionado con la resolución inicial del `invokedynamic` y la inicialización del mecanismo que produce la implementación de la lambda.

Las ejecuciones posteriores pueden beneficiarse del estado ya establecido en el call site.

---

## 🧠 Bootstrap Method y `LambdaMetafactory`

Cuando la JVM encuentra por primera vez el `invokedynamic` asociado con una lambda, debe resolver el call site.

Conceptualmente:

```text
                 invokedynamic
                      │
                      ▼
              Bootstrap Method
                      │
                      ▼
              LambdaMetafactory
                      │
                      ▼
                 Call Site
                      │
             ┌────────┴────────┐
             │                 │
             ▼                 ▼
        resolución          reutilización
        inicial             posterior
```

La resolución inicial puede tener un costo que posteriormente no necesariamente se repite de la misma manera.

---

## 🧠 Call Site

Un call site representa conceptualmente el punto dinámico donde la JVM puede establecer qué comportamiento debe ejecutarse.

En el caso de una lambda:

```text
Código Java
     │
     ▼
() -> {}
     │
     ▼
invokedynamic
     │
     ▼
Call Site
     │
     ▼
Lambda implementation
```

Una distinción importante es que el comportamiento asociado al call site puede reutilizarse durante las ejecuciones posteriores.

Por eso el experimento busca observar la diferencia entre:

```text
Primera resolución
       │
       ▼
Bootstrap
       │
       ▼
Call Site establecido
       │
       ▼
Ejecuciones posteriores
```

---

## 🧠 Consideraciones del benchmark

El benchmark no representa exclusivamente el costo de `LambdaMetafactory`.

También pueden intervenir otros factores de la JVM:

* JIT compilation
* Warm-up
* Eliminación de código
* Optimización del compilador
* Resolución del call site
* Estado del runtime
* Precisión y resolución de `System.nanoTime()`

Por esta razón, el experimento debe interpretarse como una observación del comportamiento del runtime y no como una medición aislada del costo interno de una sola operación.

---

# 🧠 Conceptos consolidados — Reto 3

* `invokedynamic`
* Bootstrap Method
* `LambdaMetafactory`
* Call Site
* Resolución dinámica
* Cacheo del Call Site
* Warm-up
* JIT Compilation
* Benchmarking con `System.nanoTime()`

---

# 🏗️ Relación con JVM y Systems Engineering

Las lambdas representan un buen ejemplo de cómo una abstracción de alto nivel puede terminar utilizando mecanismos dinámicos de la JVM.

El código:

```java
Runnable r = () -> {};
```

parece una operación sencilla desde el punto de vista del programador.

Sin embargo, conceptualmente existe una cadena de ejecución:

```text
Java Source
     │
     ▼
Lambda Expression
     │
     ▼
Bytecode
     │
     ▼
invokedynamic
     │
     ▼
Bootstrap Method
     │
     ▼
LambdaMetafactory
     │
     ▼
Call Site
     │
     ▼
Runtime implementation
     │
     ▼
JIT optimization
     │
     ▼
Native execution
```

Este modelo es especialmente importante desde una perspectiva de Systems Engineering porque permite analizar una aplicación desde diferentes niveles:

```text
Código Java
    │
    ▼
Lenguaje
    │
    ▼
Bytecode
    │
    ▼
JVM
    │
    ├── invokedynamic
    ├── Call Sites
    ├── Bootstrap Methods
    ├── LambdaMetafactory
    └── JIT
    │
    ▼
CPU
```

La abstracción del lenguaje no elimina la existencia de estos mecanismos; simplemente permite trabajar con ellos sin administrarlos directamente.

---

# 🗺️ Mapa del capítulo

```text
                 Chapter 6
                     │
          ┌──────────┴──────────┐
          │                     │
     Interfaces             Lambdas
          │                     │
          ▼                     ▼
 Functional Interface       Closure
          │                     │
          ▼                     ▼
      Predicate         Captured Variables
          │                     │
          └──────────┬──────────┘
                     │
                     ▼
              Method References
                     │
                     ▼
              Functional Composition
                     │
                     ▼
                invokedynamic
                     │
                     ▼
              Bootstrap Method
                     │
                     ▼
             LambdaMetafactory
                     │
                     ▼
                 Call Site
                     │
                     ▼
              JVM / JIT Runtime
```

---

# 📌 Conceptos consolidados del capítulo

```text
Java Functional Programming
          │
          ├── Functional Interfaces
          │       │
          │       └── @FunctionalInterface
          │
          ├── Lambda Expressions
          │       │
          │       └── Closures
          │               │
          │               └── effectively final
          │
          ├── Predicate
          │       │
          │       └── Functional Composition
          │
          ├── Method References
          │       │
          │       └── ::
          │
          └── JVM Internals
                  │
                  ├── invokedynamic
                  ├── Bootstrap Methods
                  ├── LambdaMetafactory
                  ├── Call Sites
                  └── JIT
```

---

# 🎓 Aprendizaje principal

Una lambda no debe entenderse únicamente como una forma corta de escribir código.

Desde la perspectiva de la JVM, una lambda representa una construcción dinámica que puede involucrar:

```text
Lambda
   │
   ▼
invokedynamic
   │
   ▼
Bootstrap Method
   │
   ▼
LambdaMetafactory
   │
   ▼
Call Site
   │
   ▼
Runtime behavior
```

El capítulo permite conectar tres niveles de conocimiento:

```text
Lenguaje Java
      │
      ▼
Abstracciones funcionales
      │
      ▼
Mecanismos de la JVM
```

La idea fundamental es comprender que **una construcción aparentemente sencilla del lenguaje puede involucrar mecanismos dinámicos de la JVM que afectan su comportamiento durante el runtime**.

---

## 📊 Estado del capítulo

| Reto   | Tema principal                  | Estado       |
| ------ | ------------------------------- | ------------ |
| Reto 1 | Interfaz funcional + Closure    | ✅ Completado |
| Reto 2 | Method References + composición | ✅ Completado |
| Reto 3 | `invokedynamic` + Call Sites    | ✅ Completado |

**Capítulo 6 completado: 3 / 3 retos.**
