# Chapter 5 — Inheritance & Polymorphism

📁 **Carpeta:** `05-inheritance-polymorphism/`
📄 **Archivo:** `RecordsAndPolymorphism.java`
🎯 **Retos totales del capítulo:** 2

---

## 🎯 Objetivo del capítulo

Este capítulo estudia conceptos fundamentales del modelo de objetos de Java relacionados con:

* `record`
* Inmutabilidad
* Igualdad por valor
* Identidad de objetos
* `equals()`
* Operador `==`
* Interfaces selladas
* Polimorfismo
* Dynamic Dispatch
* Ligadura dinámica de métodos

Los retos combinan el modelo de objetos de Java con una perspectiva orientada a JVM, mostrando la diferencia entre **identidad de un objeto** y **estado/valor del objeto**, así como la forma en que la JVM determina qué implementación de un método debe ejecutarse.

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals — 12th Edition*
> Cay S. Horstmann

Los ejercicios fueron diseñados como experimentos prácticos alrededor de los conceptos estudiados en el capítulo, especialmente Records, herencia, interfaces y polimorfismo.

---

# 🧪 Reto 1 — Records e inmutabilidad real

## 🎯 Objetivo

Crear un `record` para representar una fotografía del uso de CPU de un nodo:

```java id="0d1m8v"
record CpuSnapshot(
    String nodeId,
    double usagePercent,
    long timestampMs
) {}
```

El objetivo es observar experimentalmente cómo un `record` proporciona:

* una representación compacta de datos;
* componentes finales;
* igualdad basada en valores;
* `toString()` generado automáticamente;
* y una semántica orientada a objetos de datos.

---

## 🔨 Implementación

Se crean dos objetos independientes:

```java id="1j5e0f"
CpuSnapshot snapshot1 =
    new CpuSnapshot("node-01", 75.5, 1000);

CpuSnapshot snapshot2 =
    new CpuSnapshot("node-01", 75.5, 1000);
```

Aunque contienen exactamente los mismos valores, cada llamada a `new` crea una instancia diferente.

Conceptualmente:

```text id="v9y6q1"
Heap

snapshot1 ───────► CpuSnapshot A
                   │
                   ├── nodeId
                   ├── usagePercent
                   └── timestampMs


snapshot2 ───────► CpuSnapshot B
                   │
                   ├── nodeId
                   ├── usagePercent
                   └── timestampMs
```

Son dos objetos distintos, aunque representen el mismo estado.

---

# 🔮 Pregunta de predicción

Antes de ejecutar el programa se planteó:

> Si `snapshot1` y `snapshot2` son objetos diferentes creados mediante `new`, ¿qué resultado tendrán `equals()` y `==`?

### Predicción

La respuesta fue:

```text id="h8k6x2"
snapshot1.equals(snapshot2) → true
snapshot1 == snapshot2       → false
```

---

## 🧠 `equals()` vs. `==`

La diferencia fundamental es:

### `==`

Para referencias a objetos, compara **identidad de referencia**.

Pregunta conceptualmente:

> ¿Ambas referencias apuntan al mismo objeto?

```text id="p6h3bq"
snapshot1 ──┐
            ▼
         OBJETO A

snapshot2 ──► OBJETO B
```

Como son objetos diferentes:

```text
snapshot1 == snapshot2
        ↓
      false
```

---

### `equals()`

El `record` genera automáticamente una implementación de `equals()` basada en los componentes del record.

Pregunta conceptualmente:

> ¿Estos dos objetos tienen los mismos valores?

En este caso:

```text id="m7f2qa"
CpuSnapshot A
nodeId       = "node-01"
usagePercent = 75.5
timestampMs  = 1000

CpuSnapshot B
nodeId       = "node-01"
usagePercent = 75.5
timestampMs  = 1000
```

Por lo tanto:

```text id="v9w1tj"
snapshot1.equals(snapshot2)
             ↓
           true
```

---

# 🖨️ `toString()` generado automáticamente

También se imprime directamente:

```java id="j3o5kq"
System.out.println(snapshot1);
```

Un `record` proporciona automáticamente una representación textual basada en sus componentes.

Conceptualmente, el resultado tiene la forma:

```text id="8b1f1j"
CpuSnapshot[nodeId=node-01, usagePercent=75.5, timestampMs=1000]
```

Esto evita tener que escribir manualmente un `toString()` básico para representar el estado del objeto.

---

# 🧠 Conceptos consolidados — Reto 1

* `record`
* Inmutabilidad
* Componentes finales
* Igualdad por valor
* Identidad de objeto
* `equals()`
* `==`
* `toString()`
* `new`
* Heap
* Objetos independientes
* Data-oriented objects

---

# 🧪 Reto 2 — Dynamic Dispatch: Polimorfismo real con jerarquía sellada

## 🎯 Objetivo

Extender el programa para representar diferentes tipos de recursos de infraestructura mediante una interfaz sellada:

```java id="1p2p8y"
sealed interface Resource
    permits CloudNode, StorageVolume {

    void shutdown();
}
```

Se implementan dos tipos concretos:

```text id="y7y5ax"
Resource
   │
   ├── CloudNode
   │
   └── StorageVolume
```

Ambas clases son `final` y proporcionan su propia implementación de:

```java id="b2x4k3"
void shutdown()
```

---

## 🧱 Implementación

Se crea un arreglo utilizando el tipo de la interfaz:

```java id="q5f2zs"
Resource[] resources = {
    new CloudNode(),
    new StorageVolume()
};
```

Posteriormente se recorre:

```java id="3x2r7a"
for (Resource r : resources) {
    r.shutdown();
}
```

No se utiliza:

* `instanceof`
* casting
* condiciones para determinar manualmente el tipo

La selección de la implementación correcta queda a cargo del mecanismo de **dynamic dispatch**.

---

# 🔮 Pregunta de predicción

Antes de ejecutar el programa se planteó:

> Si `r` está declarado como `Resource`, ¿por qué `r.shutdown()` ejecuta el método correcto de `CloudNode` o `StorageVolume`?

### Predicción

La respuesta fue:

> `r.shutdown()` se resuelve mediante **ligadura dinámica**, por lo que la JVM determina en tiempo de ejecución qué implementación de `shutdown()` corresponde a la clase real del objeto.

---

# 🧠 Static Type vs. Dynamic Type

Este reto permite diferenciar dos conceptos importantes.

### Tipo estático

El compilador ve:

```java id="j7q9bz"
Resource r
```

Por lo tanto, sabe que `r` puede invocar:

```java id="8a1t0m"
shutdown()
```

porque `Resource` declara ese método.

---

### Tipo dinámico

En tiempo de ejecución, el objeto puede ser:

```text id="9o8fca"
CloudNode
```

o:

```text id="8k2q6w"
StorageVolume
```

La JVM utiliza la clase real del objeto para determinar qué implementación debe ejecutar.

---

# ⚙️ Dynamic Dispatch

El flujo conceptual es:

```text id="x4n7lm"
              Resource r
                  │
                  ▼
          r.shutdown()
                  │
                  ▼
          Dynamic Dispatch
                  │
          ┌───────┴────────┐
          │                │
          ▼                ▼
      CloudNode       StorageVolume
          │                │
          ▼                ▼
     shutdown()        shutdown()
```

El código cliente no necesita conocer explícitamente la clase concreta.

Solamente necesita conocer el contrato:

```java id="6bq1cw"
Resource
```

Esto es una de las bases fundamentales del polimorfismo.

---

# 🔒 ¿Por qué una interfaz `sealed`?

La declaración:

```java id="8u1d3q"
sealed interface Resource
    permits CloudNode, StorageVolume
```

restringe qué clases pueden implementar `Resource`.

En este caso, la jerarquía queda explícitamente definida:

```text id="xq2d5w"
Resource
  │
  ├── CloudNode
  │
  └── StorageVolume
```

Esto permite que la jerarquía sea conocida y controlada por el diseño del programa.

---

# 🧠 Conceptos consolidados — Reto 2

* Interfaces
* `sealed`
* `permits`
* `final`
* Polimorfismo
* Tipo estático
* Tipo dinámico
* Dynamic Dispatch
* Ligadura dinámica
* Method overriding
* Contratos
* Abstracción
* Jerarquías de tipos

---

# 🏗️ Relación con JVM y Systems Engineering

Estos conceptos aparecen constantemente en sistemas reales.

Un sistema de infraestructura puede manejar diferentes tipos de recursos:

```text id="e8p6fk"
              Resource
                 │
        ┌────────┼────────┐
        │        │        │
        ▼        ▼        ▼
      Node     Volume    Network
```

El código que administra esos recursos puede trabajar contra una abstracción común:

```java id="9m3s0v"
Resource resource;
```

sin tener que conocer todos los detalles de cada implementación.

Esto permite construir componentes más desacoplados.

La JVM participa en este modelo mediante el mecanismo de **dynamic dispatch**, que permite que una llamada realizada mediante una referencia de tipo base termine ejecutando la implementación correspondiente al objeto concreto.

---

# 🗺️ Mapa del capítulo

```text id="q1w7pr"
              CHAPTER 5
       INHERITANCE & POLYMORPHISM
                    │
          ┌─────────┴─────────┐
          │                   │
          ▼                   ▼
       Reto 1              Reto 2
          │                   │
          ▼                   ▼
       Records           Polimorfismo
          │                   │
          ▼                   ▼
    Inmutabilidad        Sealed Interface
          │                   │
          ▼                   ▼
     equals()            Tipo estático
          │                   │
          ▼                   ▼
        ==              Tipo dinámico
          │                   │
          ▼                   ▼
   Igualdad por valor   Dynamic Dispatch
                              │
                              ▼
                       JVM Runtime
```

---

# 📌 Conceptos consolidados del capítulo

Al finalizar los dos retos se consolidaron:

```text id="s2h4pm"
Objects
   │
   ├── Identity
   ├── Equality
   └── Immutability
          │
          ▼
       Records
          │
          ▼
    Polymorphism
          │
          ├── Static Type
          ├── Dynamic Type
          └── Dynamic Dispatch
                    │
                    ▼
                  JVM
```

---

# 🎓 Aprendizaje principal

El capítulo permite conectar dos ideas aparentemente diferentes.

### Los Records trabajan principalmente con el valor

Dos objetos diferentes pueden representar exactamente el mismo estado:

```text id="x5j6sc"
Objeto A ──► {node-01, 75.5, 1000}
Objeto B ──► {node-01, 75.5, 1000}

A == B       → false
A.equals(B)  → true
```

### El polimorfismo trabaja con el comportamiento

Una referencia puede utilizar un contrato común:

```text id="d3q4pk"
Resource
   │
   ▼
shutdown()
   │
   ▼
implementación concreta
```

La JVM determina durante la ejecución qué implementación corresponde al objeto real.

Por tanto:

```text id="v6f2kz"
Records
   ↓
Valor e inmutabilidad

Polimorfismo
   ↓
Comportamiento dinámico

JVM
   ↓
Dynamic Dispatch
```

Estas ideas forman una base importante para comprender posteriormente diseño de APIs, contratos, colecciones, concurrencia y arquitectura de sistemas Java.

---

## 📊 Estado del capítulo

| Elemento             | Estado       |
| -------------------- | ------------ |
| Capítulo 5           | ✅ Completado |
| Retos                | 2 / 2        |
| Records              | ✅            |
| Inmutabilidad        | ✅            |
| Igualdad por valor   | ✅            |
| Identidad de objetos | ✅            |
| Interfaces selladas  | ✅            |
| Polimorfismo         | ✅            |
| Dynamic Dispatch     | ✅            |
| Análisis JVM         | ✅            |

**Capítulo 5 completado: 2 / 2 retos.**
