# Chapter 9 — Collections & Memory

📁 **Carpeta:** `09-collections-memory/`
📄 **Archivo:** `CollectionsAndMemory.java`
🎯 **Retos totales del capítulo:** 3

---

## 🎯 Objetivo del capítulo

Comprender cómo las principales estructuras de datos de Java afectan el rendimiento, el acceso a memoria y el comportamiento de una aplicación desde la perspectiva de Systems Engineering.

Durante este capítulo se trabajará con:

* `ArrayList` vs `LinkedList`.
* Acceso aleatorio y complejidad temporal.
* `HashMap`.
* `TreeMap` como estructura basada en árbol.
* `hashCode()` y colisiones.
* `equals()` y resolución de claves.
* Complejidad algorítmica.
* Estructuras de memoria contiguas y enlazadas.
* Ring Buffer.
* Buffers de capacidad fija.
* Índices lógicos y físicos.
* Sobreescritura circular.
* Relación entre estructuras de datos, memoria y rendimiento.

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals, 12th Edition* — Cay S. Horstmann

Las Collections de Java proporcionan estructuras de datos reutilizables, pero cada implementación tiene características diferentes de acceso, inserción, consumo de memoria y comportamiento frente a determinados patrones de uso.

Desde una perspectiva de Systems Engineering, seleccionar una estructura no consiste únicamente en saber utilizar su API.

También es necesario comprender:

```text
Estructura de datos
       │
       ├── Organización en memoria
       │
       ├── Complejidad algorítmica
       │
       ├── Patrón de acceso
       │
       └── Costos de runtime
```

Este capítulo conecta la API de Collections con el comportamiento real de las estructuras en memoria.

---

# 🧪 Reto 1 — Midiendo el costo real: `ArrayList` vs `LinkedList` en inserción y acceso

## 🎯 Objetivo

Comparar experimentalmente el costo del acceso aleatorio mediante `get(index)` sobre:

```java
ArrayList<Integer>
```

y:

```java
LinkedList<Integer>
```

utilizando una lista de `100_000` elementos y `10_000` accesos aleatorios.

La prueba utiliza `System.nanoTime()` para medir el tiempo total empleado por cada implementación.

---

# 🔮 Pregunta de predicción

> Dado que `LinkedList.get(index)` debe recorrer nodos hasta encontrar el índice solicitado, mientras `ArrayList.get(index)` puede acceder directamente a la posición correspondiente, ¿qué diferencia de orden de magnitud esperas?

### Predicción

La hipótesis inicial fue:

```text
≈ 100×
```

La diferencia podría ser incluso mayor dependiendo de:

* JVM utilizada.
* Hardware.
* Estado de calentamiento de la JVM.
* Distribución de los índices aleatorios.
* Implementación interna de la colección.
* Forma en que se realiza el benchmark.

Lo importante no es acertar exactamente el multiplicador, sino identificar la diferencia estructural entre ambas implementaciones.

---

## 🧠 `ArrayList` — acceso directo

Conceptualmente, `ArrayList` mantiene sus elementos en un arreglo interno.

```text
ArrayList
   │
   ▼
┌────┬────┬────┬────┬────┬────┐
│  0 │  1 │  2 │  3 │  4 │ ...│
└────┴────┴────┴────┴────┴────┘
   ▲
   │
 get(index)
```

El acceso mediante índice puede calcular directamente la posición correspondiente.

Por eso:

```text
get(index)
    │
    ▼
acceso directo
    │
    ▼
O(1)
```

---

## 🧠 `LinkedList` — acceso mediante recorrido

`LinkedList` utiliza nodos enlazados.

Conceptualmente:

```text
Node
 │
 ├── item
 ├── next ──────► Node
 │                 │
 │                 ├── item
 │                 └── next ──────► Node
```

Para encontrar un índice determinado, la implementación debe desplazarse por los nodos.

Además, `LinkedList` puede comenzar desde el extremo más cercano al índice:

```text
inicio ───────────────► índice
fin    ◄─────────────── índice
```

Pero continúa siendo un recorrido.

Por eso el acceso aleatorio tiene complejidad:

```text
O(n)
```

en el peor caso.

---

## 🧠 Diferencia estructural

```text
ArrayList

index ───────────────► elemento
          acceso directo

          O(1)


LinkedList

index
  │
  ▼
Node → Node → Node → Node → elemento

          recorrido

          O(n)
```

Con `10_000` accesos aleatorios, esta diferencia puede multiplicarse considerablemente.

---

# 🧠 Conceptos consolidados — Reto 1

* `ArrayList` utiliza almacenamiento basado en un arreglo.
* `ArrayList.get(index)` tiene acceso aleatorio de complejidad `O(1)`.
* `LinkedList` utiliza nodos enlazados.
* `LinkedList.get(index)` requiere recorrer nodos.
* El acceso aleatorio de `LinkedList` tiene complejidad `O(n)`.
* La estructura física de los datos afecta directamente al rendimiento.
* Un benchmark real puede variar dependiendo de JVM, hardware y condiciones de ejecución.
* La complejidad algorítmica permite anticipar diferencias de rendimiento antes de medirlas.

---

# 🧪 Reto 2 — `HashMap` vs `TreeMap`: el costo de un `hashCode()` mal implementado

## 🎯 Objetivo

Analizar cómo una implementación deficiente de `hashCode()` puede provocar colisiones masivas dentro de un `HashMap`.

Para ello se crea:

```java
class BadKey
```

cuyo método:

```java
@Override
public int hashCode() {
    return 1;
}
```

siempre devuelve el mismo valor.

También se implementa `equals()` correctamente utilizando el campo `id`.

El benchmark compara la inserción de `20_000` elementos utilizando:

```text
Integer
```

contra:

```text
BadKey
```

---

# 🔮 Pregunta de predicción

> Si las 20,000 claves `BadKey` generan exactamente el mismo hash, ¿qué complejidad esperas para la inserción completa bajo el supuesto de recorrido lineal del bucket?

### Predicción

La respuesta es:

```text
O(n²)
```

porque cada nueva inserción debe comparar la nueva clave con las claves que ya se encuentran en el mismo bucket.

Conceptualmente:

```text
Inserción 1  → 0 comparaciones
Inserción 2  → 1 comparación
Inserción 3  → 2 comparaciones
...
Inserción n  → n-1 comparaciones
```

El trabajo total se aproxima a:

```text
0 + 1 + 2 + 3 + ... + (n - 1)
```

lo que produce:

```text
O(n²)
```

---

## 🧠 El papel de `hashCode()`

Un `HashMap` utiliza el hash de la clave para determinar en qué bucket debe buscar.

En un escenario normal:

```text
Key
 │
 ▼
hashCode()
 │
 ▼
bucket
 │
 ▼
entrada
```

Con una buena distribución de hashes:

```text
Key A ──► Bucket 1
Key B ──► Bucket 7
Key C ──► Bucket 3
Key D ──► Bucket 9
```

Las claves se distribuyen entre diferentes buckets.

Pero con:

```java
return 1;
```

el escenario se convierte conceptualmente en:

```text
BadKey A ──┐
BadKey B ──┤
BadKey C ──┤
BadKey D ──┤
BadKey E ──┤
            ▼
        Bucket 1
```

Todas las claves terminan concentradas en la misma posición inicial.

---

## 🧠 `equals()` sigue siendo necesario

`hashCode()` no determina por sí solo si dos claves son iguales.

Cuando existen colisiones, `HashMap` necesita comparar las claves utilizando `equals()`.

Por eso:

```text
hashCode()
    │
    ▼
determina ubicación aproximada
    │
    ▼
equals()
    │
    ▼
determina igualdad real
```

En `BadKey`, aunque todas las claves producen:

```text
hashCode() = 1
```

cada objeto sigue teniendo un `id` diferente.

Por lo tanto:

```java
key1.equals(key2)
```

debe devolver `false` cuando los identificadores sean diferentes.

---

## 🧠 Colisiones y estructura interna de `HashMap`

Es importante distinguir entre el modelo simplificado del reto y la implementación moderna de `HashMap`.

Conceptualmente podemos imaginar:

```text
Bucket
  │
  ▼
Node → Node → Node → Node → ...
```

Sin embargo, las implementaciones modernas de Java pueden transformar un bucket con suficientes colisiones en una estructura basada en árbol.

Por eso el comportamiento real no debe interpretarse simplemente como:

```text
20,000 colisiones = necesariamente O(n²)
```

El reto utiliza la hipótesis de recorrido lineal para estudiar el costo acumulado de las comparaciones.

La idea fundamental es que una mala distribución de hashes puede degradar considerablemente el comportamiento esperado de un `HashMap`.

---

# 🧠 Conceptos consolidados — Reto 2

* `HashMap` depende de una buena distribución de `hashCode()`.
* Un `hashCode()` constante provoca colisiones masivas.
* `equals()` determina la igualdad real entre claves.
* Hashing y equality trabajan conjuntamente.
* Bajo un bucket recorrido linealmente, las inserciones acumuladas pueden alcanzar `O(n²)`.
* Las implementaciones modernas de `HashMap` pueden convertir buckets muy colisionados en árboles.
* Una API correcta de `equals()` y `hashCode()` es fundamental para estructuras hash.
* El rendimiento de una colección depende también del comportamiento de los objetos almacenados.

---

# 🧪 Reto 3 — Ring Buffer: la estructura base de `inmemory_log_buffer.java`

## 🎯 Objetivo

Construir una estructura genérica de capacidad fija:

```java
class RingBuffer<T>
```

que almacene elementos en un arreglo interno y reutilice continuamente el espacio disponible.

El buffer tendrá:

```java
private final Object[] buffer;
private int head = 0;
private int size = 0;
private final int capacity;
```

La estructura debe:

* Mantener una capacidad máxima fija.
* Insertar elementos nuevos.
* Sobreescribir el elemento más antiguo cuando está llena.
* Mantener el orden lógico de los elementos.
* Traducir índices lógicos a posiciones físicas.
* Permitir acceso mediante `get(index)`.

---

# 🔮 Pregunta de predicción

> Con capacidad `3`, después de agregar `log-A`, `log-B`, `log-C`, `log-D` y `log-E`, ¿qué tres elementos deberían permanecer y en qué orden lógico?

### Predicción

Un Ring Buffer de capacidad fija conserva los últimos `N` elementos.

Después de:

```text
log-A
log-B
log-C
log-D
log-E
```

la capacidad es:

```text
3
```

Por lo tanto, deben permanecer:

```text
log-C
log-D
log-E
```

en ese orden lógico:

```text
get(0) → log-C
get(1) → log-D
get(2) → log-E
```

---

## 🧠 El concepto de `head`

`head` representa la posición física del elemento **más antiguo**.

Inicialmente:

```text
capacity = 3

buffer:

[ 0 ][ 1 ][ 2 ]
  ▲
 head
```

Después de agregar:

```text
A → B → C
```

tenemos:

```text
[ A ][ B ][ C ]
  ▲
 head
```

El elemento más antiguo es `A`.

Cuando llega `D`, el buffer está lleno.

Por lo tanto:

```text
D
│
▼
sobrescribe A
```

y `head` avanza:

```text
[ D ][ B ][ C ]
       ▲
     head
```

Ahora `B` es el elemento más antiguo.

Cuando llega `E`:

```text
[ D ][ E ][ C ]
            ▲
          head
```

Ahora `C` es el elemento más antiguo.

El orden físico:

```text
D → E → C
```

no coincide necesariamente con el orden lógico.

El orden lógico debe obtenerse comenzando desde `head`:

```text
C → D → E
```

---

## 🧠 Índice lógico vs índice físico

Esta es una de las ideas fundamentales del Ring Buffer.

El usuario puede solicitar:

```java
get(0)
```

y esperar:

```text
elemento más antiguo
```

Pero ese elemento no necesariamente se encuentra físicamente en:

```text
buffer[0]
```

La traducción es:

```text
posición física = (head + index) % capacity
```

Conceptualmente:

```text
           head
            │
            ▼
       ┌────┬────┬────┐
       │ C  │ D  │ E  │
       └────┴────┴────┘
        0    1    2

get(0) → posición física de C
get(1) → posición física de D
get(2) → posición física de E
```

El operador `%` permite regresar al inicio del arreglo cuando se alcanza el final:

```text
2 + 1
  │
  ▼
3 % 3 = 0
```

Esto convierte el arreglo en una estructura circular.

---

## 🧠 Ring Buffer como estructura de memoria

El Ring Buffer evita crecer indefinidamente.

Conceptualmente:

```text
               capacidad fija
                     │
                     ▼
        ┌────┬────┬────┬────┬────┐
        │    │    │    │    │    │
        └────┴────┴────┴────┴────┘
                     ▲
                     │
                memoria acotada
```

Cuando llega un nuevo elemento y el buffer está lleno:

```text
nuevo elemento
      │
      ▼
sobreescribe
el más antiguo
      │
      ▼
head avanza
```

Esto resulta especialmente útil para sistemas que necesitan conservar únicamente una ventana reciente de información.

Por ejemplo:

```text
logs recientes
métricas
eventos
trazas
samples
health checks
```

---

# 🧠 Relación con `inmemory_log_buffer.java`

Este reto constituye directamente la base conceptual del proyecto:

```text
Collections & Memory
        │
        ▼
    RingBuffer
        │
        ▼
 memoria acotada
        │
        ▼
   logs recientes
        │
        ▼
inmemory_log_buffer.java
```

En un sistema de logging en memoria, mantener todos los eventos indefinidamente podría provocar un crecimiento continuo de memoria.

Un Ring Buffer establece explícitamente un límite:

```text
capacity = N
```

Por lo tanto:

```text
memoria máxima
      │
      ▼
 aproximadamente acotada
```

a diferencia de una colección que continúa creciendo mientras recibe nuevos elementos.

---

# 🧠 Conceptos consolidados — Reto 3

* Un Ring Buffer utiliza una capacidad fija.
* `head` identifica el elemento lógico más antiguo.
* `size` representa la cantidad actual de elementos.
* Cuando el buffer está lleno, el elemento más antiguo es sobrescrito.
* `head` avanza circularmente mediante aritmética modular.
* Los índices lógicos no necesariamente coinciden con las posiciones físicas.
* `(head + index) % capacity` traduce una posición lógica a una posición física.
* El Ring Buffer mantiene memoria acotada.
* Es útil para logs, métricas, eventos y ventanas de datos recientes.
* La estructura constituye la base de `inmemory_log_buffer.java`.

---

# 🏗️ Relación con JVM y Systems Engineering

Este capítulo conecta las Collections de Java con una cuestión fundamental de Systems Engineering:

> **La estructura de datos determina cómo se comporta el programa frente a la memoria y al acceso a los datos.**

Tres estructuras muestran tres modelos diferentes:

```text
Collections
    │
    ├── ArrayList
    │      │
    │      └── arreglo contiguo
    │
    ├── LinkedList
    │      │
    │      └── nodos enlazados
    │
    └── HashMap
           │
           └── hashing + buckets
```

Y el Ring Buffer introduce un cuarto modelo:

```text
RingBuffer
    │
    └── arreglo fijo + índices circulares
```

---

## 🧠 Memoria y localidad

`ArrayList` almacena referencias dentro de un arreglo.

Conceptualmente:

```text
Array
┌────┬────┬────┬────┬────┬────┐
│ref │ref │ref │ref │ref │... │
└────┴────┴────┴────┴────┴────┘
```

Los elementos se encuentran organizados de manera contigua dentro del arreglo de referencias.

En cambio, `LinkedList` requiere nodos separados:

```text
Node        Node        Node
┌──────┐    ┌──────┐    ┌──────┐
│item  │───►│item  │───►│item  │
│next  │    │next  │    │next  │
└──────┘    └──────┘    └──────┘
```

Desde la perspectiva de rendimiento, esto puede afectar no solo la complejidad algorítmica sino también el comportamiento de memoria y localidad de acceso.

---

## 🧠 Estructuras de datos y complejidad

```text
Estructura
    │
    ├── ArrayList
    │      └── get(index) → O(1)
    │
    ├── LinkedList
    │      └── get(index) → O(n)
    │
    ├── HashMap
    │      └── depende de hashing y colisiones
    │
    └── RingBuffer
           └── acceso por índice → O(1)
```

Esto muestra por qué conocer únicamente la API:

```java
list.get(index);
```

no es suficiente.

La misma operación conceptual puede tener costos completamente diferentes dependiendo de la implementación concreta.

---

# 🗺️ Mapa del capítulo

```text
                 COLLECTIONS & MEMORY
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
     ArrayList       HashMap       RingBuffer
          │              │              │
          ▼              ▼              ▼
      Array[]         Buckets       Array fijo
          │              │              │
          ▼              ▼              ▼
       O(1) get      hashCode()    índice lógico
          │          equals()            │
          │              │               ▼
          │              ▼          head + index
          │         colisiones            │
          │              │               ▼
          │              ▼             modulo
          │         rendimiento           │
          │                              ▼
          └──────────────┬───────────────┘
                         │
                         ▼
                  MEMORY AWARENESS
                         │
                         ▼
                SYSTEMS ENGINEERING
```

---

# 📌 Conceptos consolidados del capítulo

```text
COLLECTIONS
    │
    ├── ArrayList
    │     └── acceso aleatorio O(1)
    │
    ├── LinkedList
    │     └── acceso aleatorio O(n)
    │
    └── HashMap
          ├── hashCode()
          ├── equals()
          └── colisiones


MEMORY
    │
    ├── almacenamiento contiguo
    ├── nodos enlazados
    ├── buckets
    └── buffers de capacidad fija


RING BUFFER
    │
    ├── capacity
    ├── head
    ├── size
    ├── índice lógico
    ├── índice físico
    └── aritmética modular
```

---

# 🎓 Aprendizaje principal

El aprendizaje central del capítulo es que **la elección de una estructura de datos determina el costo de las operaciones que realizamos sobre ella**.

Dos colecciones pueden proporcionar una API aparentemente similar:

```java
get(index)
```

pero tener comportamientos completamente diferentes:

```text
ArrayList
   │
   └── acceso directo → O(1)


LinkedList
   │
   └── recorrido       → O(n)
```

De la misma manera, un `HashMap` depende de una correcta implementación de:

```text
hashCode()
    +
equals()
```

para mantener una buena distribución y evitar degradaciones innecesarias.

Finalmente, el Ring Buffer muestra cómo diseñar una estructura cuando existe una restricción explícita de memoria:

```text
capacidad fija
      │
      ▼
memoria acotada
      │
      ▼
sobreescritura controlada
      │
      ▼
datos recientes
```

Este concepto será utilizado directamente en:

```text
inmemory_log_buffer.java
```

y representa el paso de estudiar Collections a **diseñar estructuras de datos conscientes de memoria y rendimiento**.

---

## 📊 Estado del capítulo

| Reto   | Tema                                 | Estado       |
| ------ | ------------------------------------ | ------------ |
| Reto 1 | `ArrayList` vs `LinkedList`          | ✅ Completado |
| Reto 2 | `HashMap`, `hashCode()` y colisiones | ✅ Completado |
| Reto 3 | Ring Buffer                          | ✅ Completado |

**Capítulo 9 completado: 3 / 3 retos.**
