# Intensive Java & JVM Study

Estudio intensivo de **Java y JVM** enfocado en memoria, concurrencia, performance, programación de sistemas y fundamentos de **Platform Engineering**.

El repositorio combina el estudio teórico de:

> **Core Java, Volume I: Fundamentals — 12th Edition**
> Cay S. Horstmann

con experimentos prácticos y pequeños proyectos orientados a comprender qué ocurre **dentro de la JVM**, cómo se comporta Java en memoria y cómo diferentes decisiones de diseño afectan el rendimiento y la concurrencia.

---

## Objetivos

Este repositorio tiene como objetivos desarrollar una comprensión sólida de:

* Arquitectura y modelo de ejecución de la JVM
* Compilación y ejecución de bytecode
* JIT (Just-In-Time Compilation)
* Stack y Heap
* Referencias y objetos
* Inmutabilidad
* `static`
* `equals()` y `hashCode()`
* Polimorfismo
* Records
* Interfaces y Lambdas
* Manejo de excepciones
* Logging
* Genéricos y Type Erasure
* Collections Framework
* Complejidad temporal
* Estructuras de datos en memoria
* Threads
* Race Conditions
* `synchronized`
* Locks
* Concurrencia
* Diagnóstico y observabilidad de la JVM

La meta es pasar de:

```text
Java como lenguaje
        ↓
Java como runtime
        ↓
JVM internals
        ↓
Memory & Performance
        ↓
Concurrency
        ↓
Observability
        ↓
Platform Engineering
```

---

# Libro de estudio

## Core Java, Volume I: Fundamentals — 12th Edition

**Autor:** Cay S. Horstmann

El libro es la base principal para el estudio de Java.

La ruta no sigue necesariamente todos los capítulos del libro de forma lineal. Se seleccionaron los capítulos y conceptos que tienen mayor relación con el objetivo de estudiar Java desde una perspectiva de:

* JVM
* Systems Programming
* Backend Engineering
* Performance
* Concurrency
* Platform Engineering

---

# Roadmap de estudio

## Semana 1 — Arquitectura JVM

### Capítulo 1 — Introduction to Java

### Conceptos estudiados

* Funcionamiento del ejecutable `.jar`
* Compilación Java
* Bytecode
* Modelo de ejecución de la JVM
* JIT (Just-In-Time Compilation)
* Runtime de Java
* Identidad de la JVM
* Procesos y PID
* Procesadores disponibles

### Proyecto

`01-jvm-architecture/jvm_memory_inspector.java`

Inspector CLI orientado a estudiar:

* identidad del runtime
* JVM utilizada
* versión de Java
* proceso JVM
* procesadores disponibles
* medición de ejecución
* comportamiento del JIT mediante warm-up

---

## Semana 1 — Arquitectura JVM

### Capítulo 3 — Fundamental Programming Structures

### Conceptos estudiados

* Tipos primitivos
* Tipos referencia
* Variables
* Referencias
* Stack
* Heap
* String
* Inmutabilidad
* Paso de referencias
* Modelo de memoria conceptual

### Integración

Los conceptos de este capítulo se integraron con el proyecto de arquitectura JVM.

La intención fue comprender la diferencia entre:

```text
Variable primitiva
       ↓
     valor

Variable referencia
       ↓
     referencia
       ↓
      Heap
       ↓
     objeto
```

---

# Semana 1 — Memoria & Polimorfismo

## Capítulo 4 — Objects and Classes

### Conceptos estudiados

* Objetos
* Clases
* Constructores
* Métodos
* Campos
* `static`
* Estado de los objetos
* Referencias
* Heap
* Ciclo de vida conceptual de objetos
* `hashCode()`
* Validación de objetos

### Proyecto

`04-objects-classes/cloud_resource_model.java`

Modelo CLI de recursos cloud orientado a estudiar:

* objetos
* referencias
* validación
* identidad
* representación de recursos
* comportamiento de `hashCode()`

---

# Semana 1 — Memoria & Polimorfismo

## Capítulo 5 — Inheritance

### Conceptos estudiados

* Herencia
* Polimorfismo
* Clase raíz `Object`
* `equals()`
* `hashCode()`
* Contratos de igualdad
* Inmutabilidad
* Records
* Comportamiento de objetos en colecciones

### Integración

Los conceptos de herencia, igualdad e inmutabilidad se integraron con el modelo desarrollado en el capítulo anterior.

Especial atención a:

```text
equals()
   +
hashCode()
   ↓
Collections
   ↓
HashMap / HashSet
```

---

# Semana 2 — Contratos & Trazabilidad

## Capítulo 6 — Interfaces & Lambda Expressions

### Conceptos estudiados

* Interfaces
* Contratos
* Desacoplamiento
* Interfaces funcionales
* Lambda expressions
* Method references
* Composición de comportamiento

### Proyecto

`06-interfaces-lambda/resilient_log_writer.java`

Pipeline de procesamiento de eventos orientado a estudiar:

* interfaces
* lambdas
* procesamiento desacoplado
* manejo de errores de I/O
* resiliencia

---

# Semana 2 — Contratos & Trazabilidad

## Capítulo 7 — Exceptions, Assertions & Logging

### Conceptos estudiados

* Jerarquía de excepciones
* Checked exceptions
* Unchecked exceptions
* `try/catch`
* `throws`
* Manejo de errores
* Assertions
* Logging
* `java.util.logging`
* Prevención de fallos de servicio

### Integración

Estos conceptos se incorporaron al proyecto del Capítulo 6 para construir un flujo más resistente ante errores de I/O.

Modelo conceptual:

```text
Evento
  ↓
Procesamiento
  ↓
I/O
  ↓
Exception
  ↓
Captura
  ↓
Logging
  ↓
Recuperación
  ↓
Proceso continúa
```

---

# Semana 3 — Genéricos & Colecciones

## Capítulo 8 — Generic Programming

### Conceptos estudiados

* Generic types
* Type parameters
* Type safety
* Type Erasure
* Compilación vs Runtime
* Comportamiento de tipos genéricos dentro de la JVM

### Proyecto

`08-generic-programming/inmemory_log_buffer.java`

Búfer circular orientado al almacenamiento temporal de telemetría.

El proyecto estudia estructuras de memoria de tamaño acotado y acceso eficiente.

---

# Semana 3 — Genéricos & Colecciones

## Capítulo 9 — Collections

### Conceptos estudiados

* `ArrayList`
* `LinkedList`
* `HashMap`
* Complejidad temporal
* Acceso a memoria
* Hashing
* Colisiones
* `hashCode()`
* Distribución de elementos
* Costos de búsqueda
* Estructuras dinámicas
* Ring Buffer
* Complejidad `O(n²)`
* Complejidad `O(1)` esperada en hashing
* Impacto de una mala función hash

### Proyecto

`09-collections-memory/CollectionsAndMemory.java`

El proyecto contiene experimentos comparativos entre estructuras de datos.

Se estudiaron experimentalmente:

```text
ArrayList
LinkedList
HashMap
BadHash
RingBuffer
```

### Experimento de HashMap

Se construyó una clave con una función hash deliberadamente deficiente para observar el efecto de las colisiones.

Comparación conceptual:

```text
HashMap con hash normal
        ↓
distribución
        ↓
acceso esperado eficiente


HashMap con BadHash
        ↓
colisiones
        ↓
contención estructural
        ↓
mayor costo
```

### Ring Buffer

También se implementó una estructura circular de capacidad fija:

```text
capacity = 3

A → B → C
    ↓
D reemplaza A

B → C → D
    ↓
E reemplaza B

C → D → E
```

El experimento permitió estudiar:

* arrays de tamaño fijo
* índices
* memoria acotada
* sobreescritura
* complejidad constante
* estructuras útiles para buffers y telemetría

---

# Semana 4 — Concurrencia & Diagnóstico

## Capítulo 12 — Concurrency

### Conceptos estudiados

* Threads
* Ciclo de vida de un thread
* `Runnable`
* `Thread`
* `start()`
* `join()`
* Race Conditions
* Operaciones no atómicas
* Exclusión mutua
* `synchronized`
* Monitores
* Contención
* Costos de sincronización
* Concurrencia
* Lost Updates

### Proyecto

`12-concurrency-threads/ConcurrencyAndThreads.java`

---

## Reto 1 — Race Condition

Se construyó un contador compartido sin sincronización:

```java
counter++;
```

Se ejecutaron:

```text
10 threads
×
100,000 incrementos
=
1,000,000 incrementos esperados
```

El resultado observado fue menor debido a actualizaciones perdidas.

Ejemplo conceptual:

```text
Thread A             Thread B

read 100             read 100
   ↓                    ↓
+1                   +1
   ↓                    ↓
write 101            write 101

resultado = 101
```

Dos incrementos solicitados terminaron produciendo un solo incremento observable.

---

# Reto 2 — synchronized

Se implementó:

```java
static synchronized void incrementSynchronized() {
    synchronizedCounter++;
}
```

El objetivo fue comparar:

```text
sin sincronización
        vs
synchronized
```

### Resultado observado

La versión sincronizada produjo:

```text
1,000,000
```

mientras que la versión sin sincronización produjo un valor menor.

También se midió el tiempo de ejecución.

Ejemplo observado durante el experimento:

```text
Unsynchronized time: 6,719,625 ns

Synchronized time: 67,509,208 ns
```

El experimento permitió observar el costo de la exclusión mutua y la contención.

> Los tiempos son resultados de una ejecución concreta y no deben interpretarse como benchmarks universales.

---

# Semana 4 — Diagnóstico de la JVM

## Terminal / JDK — JVM Tuning & Observability

### Herramientas estudiadas

* `jps`
* `jstat`
* `jcmd`

### Conceptos

* Identificación de procesos JVM
* PID
* Inspección de procesos Java
* Métricas del runtime
* Diagnóstico desde terminal
* Observabilidad
* Información del proceso
* Relación entre aplicación y JVM

El objetivo es conectar:

```text
Aplicación Java
      ↓
JVM
      ↓
Proceso OS
      ↓
Herramientas JDK
      ↓
Observabilidad
```

---

# Mapa general de aprendizaje

```text
                 CORE JAVA
                    │
                    ▼
             JVM Architecture
                    │
          ┌─────────┴─────────┐
          ▼                   ▼
       Memory              Execution
          │                   │
     Stack / Heap            JIT
          │                   │
          └─────────┬─────────┘
                    ▼
             Objects / Classes
                    │
                    ▼
              Polymorphism
                    │
                    ▼
          Interfaces / Lambdas
                    │
                    ▼
             Exceptions
                    │
                    ▼
              Generics
                    │
                    ▼
              Collections
                    │
                    ▼
             Data Structures
                    │
                    ▼
              Concurrency
                    │
          ┌─────────┴─────────┐
          ▼                   ▼
       Threads            Synchronization
          │                   │
          └─────────┬─────────┘
                    ▼
             Race Conditions
                    │
                    ▼
              Diagnostics
                    │
                    ▼
              JVM Tools
                    │
                    ▼
          Platform Engineering
```

---

# Estructura del repositorio

```text
JAVA/
│
├── 01-jvm-architecture/
│   └── jvm_memory_inspector.java
│
├── 04-objects-classes/
│   └── cloud_resource_model.java
│
├── 06-interfaces-lambda/
│   └── resilient_log_writer.java
│
├── 08-generic-programming/
│   └── inmemory_log_buffer.java
│
├── 09-collections-memory/
│   └── CollectionsAndMemory.java
│
├── 12-concurrency-threads/
│   └── ConcurrencyAndThreads.java
│
└── README.md
```

---

# Enfoque técnico

Este repositorio no pretende ser solamente una colección de ejercicios de Java.

Cada ejercicio busca responder una pregunta técnica:

### ¿Qué ocurre realmente?

En lugar de limitarse a:

```java
HashMap<Integer, Integer>
```

se estudia:

```text
Hashing
   ↓
hashCode()
   ↓
Distribución
   ↓
Colisiones
   ↓
Estructura interna
   ↓
Costo de acceso
```

En lugar de solamente utilizar:

```java
synchronized
```

se estudia:

```text
Thread
   ↓
Shared State
   ↓
Race Condition
   ↓
Lost Update
   ↓
Monitor
   ↓
Exclusion Mutual
   ↓
Contention
```

El objetivo final es comprender Java no solamente como lenguaje, sino como **runtime ejecutándose sobre un sistema operativo y una JVM**.

---

# Relación con Platform Engineering

Los conceptos estudiados forman una base para áreas posteriores:

```text
Core Java
    ↓
JVM
    ↓
Memory Management
    ↓
Concurrency
    ↓
Performance
    ↓
Observability
    ↓
Systems Engineering
    ↓
Platform Engineering
```

Este enfoque busca construir fundamentos que posteriormente puedan aplicarse a:

* Backend Engineering
* Distributed Systems
* DevOps
* SRE
* Platform Engineering
* Cloud Infrastructure
* Performance Engineering
* JVM Operations

---

# Estado del estudio

| Semana | Área                       | Capítulos / temas | Estado       |
| ------ | -------------------------- | ----------------- | ------------ |
| 1      | Arquitectura JVM           | Cap. 1, 3         | ✅ Completado |
| 1      | Memoria & Polimorfismo     | Cap. 4, 5         | ✅ Completado |
| 2      | Contratos & Trazabilidad   | Cap. 6, 7         | ✅ Completado |
| 3      | Genéricos & Colecciones    | Cap. 8, 9         | ✅ Completado |
| 4      | Concurrencia & Diagnóstico | Cap. 12           | ✅ Completado |
| 4      | JVM Observability          | JDK / Terminal    | ✅ Completado |

---

# Referencia principal

**Horstmann, Cay S.**

*Core Java, Volume I: Fundamentals, 12th Edition.*

Este libro constituye la referencia principal utilizada para estudiar los fundamentos de Java durante esta etapa.

---

## Próxima etapa

Después de consolidar estos fundamentos, el estudio puede continuar hacia áreas más avanzadas:

```text
Core Java
    ↓
JVM Internals
    ↓
Advanced Concurrency
    ↓
JVM Performance
    ↓
Networking
    ↓
Backend Engineering
    ↓
Distributed Systems
    ↓
Platform Engineering
```

---

## Autor

**Hugo Onofre**

Java / JVM Study
Systems Programming • Backend • Platform Engineering
