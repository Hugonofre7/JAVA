# Chapter 8 — Generics & Type Erasure

📁 **Carpeta:** `08-generics-type-erasure/`
📄 **Archivo:** `GenericsAndTypeErasure.java`
🎯 **Retos totales del capítulo:** 3

---

## 🎯 Objetivo del capítulo

Comprender cómo funcionan los **Generics en Java** desde el punto de vista del compilador y de la JVM, especialmente la diferencia entre la información disponible durante compilación y la información disponible durante runtime.

Durante este capítulo se trabajará con:

* Type Erasure.
* Tipos genéricos y clases reales en runtime.
* Reflexión sobre parámetros de tipo.
* Bounded Types.
* `extends Number`.
* Wildcards.
* `? extends`.
* `? super`.
* Covarianza y contravarianza práctica.
* Restricciones de lectura y escritura en colecciones genéricas.
* Relación entre Generics, bytecode y JVM.

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals, 12th Edition* — Cay S. Horstmann

Los Generics proporcionan seguridad de tipos principalmente durante la **compilación**. Sin embargo, debido a **type erasure**, gran parte de la información específica de los parámetros genéricos no existe como tipo concreto durante el runtime.

Esto significa que:

```java
Box<String>
```

y:

```java
Box<Integer>
```

son tipos diferentes para el compilador, pero ambas referencias pertenecen a la misma clase real:

```java
Box
```

El capítulo también muestra cómo los bounds y wildcards permiten expresar restricciones sobre los tipos genéricos sin perder flexibilidad en el diseño de APIs.

---

# 🧪 Reto 1 — Type erasure en acción: comparando clases en runtime

## 🎯 Objetivo

Crear una clase genérica `Box<T>` y comprobar experimentalmente qué ocurre cuando dos instancias utilizan diferentes parámetros de tipo.

Se utilizan:

```java
Box<String>
```

y:

```java
Box<Integer>
```

para comprobar qué devuelve `.getClass()` durante runtime.

Además, mediante reflexión:

```java
Box.class.getTypeParameters().length
```

se comprueba que la declaración de la clase sí contiene información sobre que `Box` fue declarada con un parámetro de tipo.

---

# 🔮 Pregunta de predicción

> `Box<String>` y `Box<Integer>` son tipos claramente distintos en el código fuente, pero debido a type erasure, ¿esperas que `stringBox.getClass() == intBox.getClass()` imprima `true` o `false`?

### Predicción

La respuesta esperada es:

```text
true
```

La razón está en la diferencia entre el **tipo genérico utilizado en el código fuente** y la **clase real existente durante runtime**.

`Box<String>` y `Box<Integer>` son tipos distintos desde el punto de vista del compilador.

Sin embargo, después de aplicar type erasure, ambos utilizan la misma clase runtime:

```text
Box<String> ─────┐
                 ├──► Box.class
Box<Integer> ────┘
```

Por eso:

```java
stringBox.getClass() == intBox.getClass()
```

resulta:

```text
true
```

## 🧠 ¿Qué consulta realmente `.getClass()`?

`.getClass()` no pregunta:

> "¿Qué parámetro genérico tenía esta variable en el código fuente?"

Pregunta:

> "¿Cuál es la clase runtime del objeto que está referenciando esta referencia?"

Por lo tanto:

```java
stringBox.getClass()
```

y:

```java
intBox.getClass()
```

devuelven la misma clase runtime:

```text
class Box
```

El parámetro genérico no convierte a `Box<String>` y `Box<Integer>` en dos clases runtime diferentes.

---

## 🧠 Type Erasure

El modelo conceptual es:

```text
CÓDIGO FUENTE

Box<String> stringBox
Box<Integer> intBox
       │
       ▼
   COMPILADOR
       │
       ▼
TYPE ERASURE
       │
       ▼
BYTECODE / RUNTIME

        Box
         ▲
         │
    ┌────┴────┐
    │         │
String      Integer
```

La seguridad relacionada con `String` e `Integer` se verifica principalmente durante compilación.

En runtime, ambos objetos pertenecen a la misma clase:

```java
Box.class
```

---

## 🧠 Reflexión y parámetros de tipo

Existe una diferencia importante entre:

```java
stringBox.getClass()
```

y:

```java
Box.class.getTypeParameters()
```

La primera consulta la clase runtime del objeto.

La segunda consulta la **declaración de la clase**.

Por eso:

```java
Box.class.getTypeParameters().length
```

devuelve:

```text
1
```

porque `Box` fue declarada como:

```java
class Box<T>
```

Esto no significa que runtime conozca que una instancia concreta contiene `String` o `Integer`.

Significa que la declaración de `Box` contiene un parámetro de tipo llamado `T`.

---

# 🧠 Conceptos consolidados — Reto 1

* `Box<String>` y `Box<Integer>` son tipos diferentes en compilación.
* Type erasure elimina la especialización concreta del parámetro genérico.
* `.getClass()` consulta la clase real del objeto durante runtime.
* Ambas instancias pertenecen a `Box.class`.
* `getClass()` no conserva `String` o `Integer` como tipo runtime de la instancia.
* `Box.class.getTypeParameters()` permite inspeccionar la declaración genérica de la clase.
* Generics proporcionan principalmente seguridad de tipos durante compilación.

---

# 🧪 Reto 2 — Bounded Types: cuándo `<T>` necesita una restricción

## 🎯 Objetivo

Crear una clase genérica acotada:

```java
class NumericBox<T extends Number>
```

para comprobar cómo un **bound** proporciona información adicional al compilador.

El método:

```java
double doubleValue()
```

podrá utilizar:

```java
value.doubleValue()
```

sin realizar ningún cast explícito.

También se comprobará que `String` no puede utilizarse como parámetro de tipo porque no cumple la restricción:

```java
T extends Number
```

---

# 🔮 Pregunta de predicción

> Si `NumericBox<T>` no tuviera el bound `extends Number`, ¿por qué el compilador rechazaría `value.doubleValue()`?

### Predicción

Sin un bound explícito:

```java
class NumericBox<T>
```

el compilador debe tratar `T` como si fuera un tipo desconocido con las capacidades mínimas garantizadas.

Conceptualmente:

```text
T
│
▼
Object
```

`Object` no posee:

```java
doubleValue()
```

Por lo tanto, esta operación no sería válida:

```java
value.doubleValue();
```

Al declarar:

```java
T extends Number
```

el compilador recibe una garantía:

```text
T
│
▼
Number
│
├── Integer
├── Double
├── Long
└── ...
```

Por lo tanto puede asumir que cualquier `T` utilizado por `NumericBox` dispone de los métodos definidos por `Number`, incluyendo:

```java
doubleValue()
```

---

## 🧠 El bound como contrato para el compilador

Sin bound:

```java
class NumericBox<T>
```

el compilador no puede asumir operaciones específicas sobre `T`.

Con bound:

```java
class NumericBox<T extends Number>
```

el compilador conoce una interfaz mínima de capacidades:

```text
          Number
            ▲
            │
      ┌─────┼─────┐
      │     │     │
   Integer Double Long
      │     │     │
      └─────┴─────┘
            │
            ▼
            T
```

Por eso:

```java
value.doubleValue()
```

es válido.

---

## 🧠 Bounded Types y seguridad de tipos

El bound también impide utilizar tipos incompatibles.

Por ejemplo:

```java
NumericBox<String> stringNumBox = new NumericBox<>();
```

produciría un error de compilación porque:

```text
String
  ✗
  │
  └── no extiende Number
```

Mientras que:

```java
NumericBox<Integer>
NumericBox<Double>
```

sí cumplen la restricción.

---

# 🧠 Conceptos consolidados — Reto 2

* Un bound restringe los tipos permitidos por un parámetro genérico.
* `T extends Number` garantiza que `T` es `Number` o una subclase.
* El compilador puede utilizar métodos definidos por `Number`.
* `doubleValue()` puede utilizarse sin cast.
* Sin bound, `T` no garantiza que exista `doubleValue()`.
* Los bounds proporcionan capacidades conocidas al compilador.
* Los bounds permiten diseñar APIs genéricas más seguras y expresivas.

---

# 🧪 Reto 3 — Wildcards: `? extends` vs `? super` en la práctica

## 🎯 Objetivo

Aplicar wildcards para comprender la diferencia entre:

```java
? extends Number
```

y:

```java
? super Integer
```

Se implementarán dos operaciones con comportamientos opuestos:

```java
static double sumAll(List<? extends Number> numbers)
```

para leer valores numéricos y:

```java
static void addIntegers(List<? super Integer> list)
```

para insertar valores `Integer`.

Este reto conecta directamente Generics con el diseño de APIs flexibles.

---

# 🔮 Pregunta de predicción

> En `sumAll(List<? extends Number> numbers)`, ¿por qué el compilador permite leer elementos pero prohíbe hacer `numbers.add(5)`?

### Predicción

Porque `? extends Number` significa:

> "La lista contiene algún tipo desconocido que es `Number` o una subclase de `Number`."

Ese tipo podría ser:

```java
List<Integer>
```

o:

```java
List<Double>
```

o:

```java
List<Long>
```

El compilador sabe que cualquier elemento leído es al menos un `Number`:

```text
List<? extends Number>
          │
          ▼
       Number
          ▲
     ┌────┼────┐
     │    │    │
 Integer Double Long
```

Por eso podemos hacer:

```java
Number value = numbers.get(i);
```

y posteriormente:

```java
value.doubleValue();
```

Pero no podemos agregar:

```java
numbers.add(5);
```

porque no sabemos cuál es el tipo concreto de la lista.

Si realmente fuera:

```java
List<Double>
```

agregar un `Integer` rompería la seguridad de tipos.

---

## 🧠 `? extends` — Productor

Una forma práctica de recordar:

```text
? extends Number
```

significa:

> La colección produce valores que puedo tratar como `Number`.

Por eso:

```java
Number n = numbers.get(i);
```

es seguro.

Pero:

```java
numbers.add(...)
```

no es seguro.

---

## 🧠 `? super` — Consumidor

En cambio:

```java
List<? super Integer>
```

garantiza que la lista puede aceptar un `Integer`.

Por eso podemos hacer:

```java
list.add(1);
list.add(2);
list.add(3);
```

La lista podría ser:

```java
List<Integer>
```

o:

```java
List<Number>
```

o incluso:

```java
List<Object>
```

Todas pueden almacenar un `Integer`.

---

## 🧠 Visualización de `extends` vs `super`

```text
             Number
               ▲
       ┌───────┼────────┐
       │       │        │
    Integer  Double    Long


? extends Number

    List<Integer>
    List<Double>
    List<Long>

         │
         ▼
       PUEDO
        LEER
         │
         ▼
       Number
```

Mientras que:

```text
? super Integer

    List<Integer>
         ▲
         │
    List<Number>
         ▲
         │
     List<Object>

         │
         ▼
      PUEDO
      ESCRIBIR
      Integer
```

La dirección de la garantía es diferente.

---

## 🧠 ¿`addIntegers(intList)` compila?

Sí.

Dado:

```java
List<Integer> intList
```

y:

```java
static void addIntegers(List<? super Integer> list)
```

la llamada:

```java
addIntegers(intList);
```

es válida.

`List<Integer>` cumple:

```text
List<Integer>
      │
      ▼
List<? super Integer>
```

Por lo tanto, tanto:

```java
List<Integer>
```

como:

```java
List<Number>
```

pueden recibir valores `Integer`.

---

## 🧠 PECS

Este comportamiento se relaciona con una regla clásica para diseñar APIs genéricas:

```text
PECS

Producer Extends
Consumer Super
```

Si una colección principalmente **produce** valores:

```java
List<? extends Number>
```

Si una colección principalmente **consume** valores:

```java
List<? super Integer>
```

No es simplemente una regla sintáctica: expresa qué operaciones son seguras sobre el tipo desconocido.

---

# 🧠 Conceptos consolidados — Reto 3

* `? extends Number` permite leer valores como `Number`.
* `? extends` representa un tipo concreto desconocido dentro de una jerarquía.
* No podemos insertar arbitrariamente valores en `List<? extends Number>`.
* `? super Integer` permite insertar `Integer`.
* `List<Integer>` es compatible con `List<? super Integer>`.
* `List<Number>` también es compatible con `List<? super Integer>`.
* `extends` se utiliza principalmente para productores.
* `super` se utiliza principalmente para consumidores.
* PECS significa **Producer Extends, Consumer Super**.

---

# 🏗️ Relación con JVM y Systems Engineering

Generics parecen una característica exclusivamente relacionada con el lenguaje Java, pero su comportamiento está directamente conectado con la arquitectura del compilador y la JVM.

El flujo conceptual es:

```text
Código Java
    │
    ▼
Generics
    │
    ▼
Compilador
    │
    ├── verifica seguridad de tipos
    │
    ├── valida bounds
    │
    ├── valida wildcards
    │
    └── aplica type erasure
            │
            ▼
         Bytecode
            │
            ▼
           JVM
            │
            ▼
         Runtime
```

Una diferencia fundamental es que gran parte de la información genérica utilizada por el programador pertenece al **sistema de tipos del compilador**, no al modelo de clases concretas utilizado por la JVM durante runtime.

Por ejemplo:

```java
Box<String>
Box<Integer>
```

no representan dos clases runtime diferentes.

Conceptualmente:

```text
                COMPILACIÓN

        Box<String>     Box<Integer>
              │               │
              └───────┬───────┘
                      │
                 Type Erasure
                      │
                      ▼

                  RUNTIME

                    Box
```

Los bounds, por otro lado, permiten al compilador conocer capacidades adicionales:

```java
T extends Number
```

permite generar y validar código basado en el contrato proporcionado por `Number`.

Los wildcards permiten expresar relaciones entre tipos sin crear una nueva clase runtime para cada combinación posible.

---

## 🧠 Generics y diseño de APIs

Desde una perspectiva de Systems Engineering, esto es importante porque una API genérica debe expresar correctamente qué puede:

```text
LEER
```

y qué puede:

```text
ESCRIBIR
```

Por ejemplo:

```java
List<? extends Number>
```

expresa una API orientada a lectura.

Mientras:

```java
List<? super Integer>
```

expresa una API que necesita insertar `Integer`.

Esto permite diseñar componentes reutilizables sin sacrificar seguridad de tipos.

El concepto será especialmente importante al trabajar posteriormente con estructuras como:

```text
inmemory_log_buffer.java
```

donde una API genérica puede necesitar aceptar diferentes implementaciones o jerarquías de datos sin perder las garantías del compilador.

---

# 🗺️ Mapa del capítulo

```text
                  GENERICS
                     │
          ┌──────────┼──────────┐
          │          │          │
          ▼          ▼          ▼
     Type Erasure  Bounds    Wildcards
          │          │          │
          │          │      ┌───┴────┐
          │          │      │        │
          ▼          ▼      ▼        ▼
      Runtime     T extends ? extends ? super
          │       Number       │        │
          │          │         │        │
          ▼          ▼         ▼        ▼
       Box.class  Capabilities Read   Write
          │          │
          ▼          ▼
      Reflection  Compile-time
          │        safety
          └──────────┬───────────────┐
                     │               │
                     ▼               ▼
                  Compiler        JVM
```

---

# 📌 Conceptos consolidados del capítulo

```text
GENERIC TYPES
     │
     ├── Compile Time
     │      │
     │      ├── Type Safety
     │      ├── Bounds
     │      └── Wildcards
     │
     └── Runtime
            │
            ├── Type Erasure
            ├── Box.class
            └── JVM Class Metadata


BOUNDS
   │
   └── T extends Number
            │
            └── Garantiza capacidades


WILDCARDS
   │
   ├── ? extends Number
   │       └── Producer → lectura
   │
   └── ? super Integer
           └── Consumer → escritura


PECS
 │
 ├── Producer → Extends
 └── Consumer → Super
```

---

# 🎓 Aprendizaje principal

El punto central del capítulo es comprender que **un tipo genérico del código fuente no necesariamente representa una clase diferente en runtime**.

```text
CÓDIGO FUENTE

Box<String>
Box<Integer>

       │
       ▼

TYPE ERASURE

       │
       ▼

RUNTIME

Box
```

Los Generics proporcionan seguridad de tipos principalmente durante compilación.

Los **bounded types** permiten restringir los tipos aceptados y proporcionan capacidades conocidas al compilador:

```java
T extends Number
```

Los **wildcards** permiten diseñar APIs flexibles expresando qué relaciones entre tipos son seguras:

```java
? extends → productor
? super   → consumidor
```

La combinación de estos conceptos permite escribir APIs genéricas que sean:

```text
SEGURAS
   +
FLEXIBLES
   +
REUTILIZABLES
```

sin depender de casts innecesarios y manteniendo las garantías del sistema de tipos de Java.

---

## 📊 Estado del capítulo

| Reto   | Tema                            | Estado       |
| ------ | ------------------------------- | ------------ |
| Reto 1 | Type Erasure + Runtime Class    | ✅ Completado |
| Reto 2 | Bounded Types                   | ✅ Completado |
| Reto 3 | Wildcards: `extends` vs `super` | ✅ Completado |

**Capítulo 8 completado: 3 / 3 retos.**
