# Chapter 1 — JVM Architecture

📁 **Carpeta:** `01-jvm-architecture/`
📄 **Archivo:** `jvm_memory_inspector.java`
🎯 **Retos totales del capítulo:** 1

---

## 🎯 Objetivo del capítulo

Este capítulo introduce la arquitectura de ejecución de Java desde la perspectiva de la **JVM**, utilizando un pequeño programa para inspeccionar la identidad del runtime y observar experimentalmente el comportamiento del **JIT Compiler** durante la ejecución repetida de un mismo código.

El objetivo no es únicamente escribir código Java, sino comenzar a analizar qué ocurre debajo del lenguaje:

* ¿Qué JVM está ejecutando el programa?
* ¿Qué versión del runtime está activa?
* ¿Dónde está instalado el JDK/JRE utilizado?
* ¿Qué PID tiene el proceso Java?
* ¿Cuántos procesadores lógicos detecta la JVM?
* ¿Por qué el mismo bytecode puede presentar diferentes perfiles de rendimiento dependiendo de la JVM?
* ¿Por qué una misma operación puede cambiar su tiempo de ejecución después de varias ejecuciones?

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals — 12th Edition*
> Cay S. Horstmann

El capítulo utiliza los conceptos relacionados con la ejecución de programas Java y los conecta con aspectos internos de la JVM y del entorno de ejecución.

Los ejercicios fueron diseñados como **experimentos prácticos alrededor de los conceptos estudiados**, no como una reproducción directa de los ejercicios del libro.

---

# 🧪 Reto 1 — Identidad del Runtime + Benchmark de Warm-up JIT

## 🎯 Objetivo

Construir `JvmMemoryInspector` para obtener información del runtime de Java y realizar un pequeño experimento de rendimiento sobre un bucle ejecutado repetidamente dentro del mismo proceso JVM.

El reto tiene dos partes:

1. **Inspección del runtime**
2. **Observación del comportamiento del JIT mediante warm-up**

---

## 1. Identidad del Runtime

Se implementó el método:

```java
static void printRuntimeIdentity()
```

El método obtiene información directamente desde la JVM:

* `Runtime.version()`
* `System.getProperty("java.vm.name")`
* `System.getProperty("java.home")`
* `ProcessHandle.current().pid()`
* `Runtime.getRuntime().availableProcessors()`

Esto permite identificar el entorno concreto en el que está ejecutándose el programa.

### Información observada

Durante la ejecución se obtuvo:

```text
Runtime version: 22.0.2+9-70
JVM name: Java HotSpot(TM) 64-Bit Server VM
Java home: /Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
Available processors: 8
```

El proceso también expuso su PID mediante:

```java
ProcessHandle.current().pid()
```

Este PID permitió relacionar la aplicación Java con el proceso observado desde herramientas externas del JDK, particularmente `jps -l`.

---

## 2. Observabilidad del proceso con `jps`

Una parte del ejercicio consistió en ejecutar el programa compilado mientras permanecía activo y utilizar:

```text
jps -l
```

desde otra terminal.

El objetivo fue comprobar que el PID reportado por:

```java
ProcessHandle.current().pid()
```

correspondiera con el proceso Java identificado mediante `jps`.

### Concepto demostrado

La aplicación puede obtener información de su propio proceso desde dentro de la JVM, mientras que herramientas externas del JDK pueden observar ese mismo proceso desde fuera.

Conceptualmente:

```text
Aplicación Java
      │
      │ ProcessHandle.current().pid()
      ▼
     PID
      ▲
      │
      │ jps -l
      │
Herramienta externa del JDK
```

Esto constituye una primera aproximación a la **observabilidad de procesos Java**.

---

# ⚙️ 3. Benchmark de Warm-up del JIT

Se implementó:

```java
static long benchmarkTightLoop(long iterations)
```

El método ejecuta un bucle durante un número determinado de iteraciones y mide el tiempo utilizando:

```java
System.nanoTime()
```

La medición se realiza antes y después de la ejecución del bucle:

```text
inicio
  │
  ▼
System.nanoTime()
  │
  ▼
ejecución del bucle
  │
  ▼
System.nanoTime()
  │
  ▼
tiempo transcurrido
```

Se utilizaron tres tamaños:

```text
1,000
1,000,000
100,000,000
```

Cada uno se ejecutó **tres veces dentro del mismo proceso JVM**, permitiendo observar el comportamiento del código después de varias ejecuciones.

---

## 🔎 Pregunta de predicción

Antes de ejecutar el benchmark se planteó:

> ¿La primera ejecución de `100_000_000` iteraciones será más lenta, igual o más rápida que la tercera ejecución?

### Predicción

La predicción fue:

> Espero que la primera ejecución sea más lenta que la tercera, porque durante las primeras ejecuciones la JVM puede estar calentando y el JIT todavía puede estar recopilando información y optimizando el código. Después de varias ejecuciones, espero que el bucle se ejecute de forma más eficiente.

---

## 🧠 Análisis

La hipótesis está relacionada con el **JIT Compiler** de la JVM.

Java no necesariamente ejecuta todo el código directamente como instrucciones nativas optimizadas desde el primer momento. Durante la ejecución, la JVM puede recopilar información sobre el comportamiento del código y utilizar el compilador JIT para generar y optimizar código nativo para las partes consideradas relevantes o "calientes".

El modelo conceptual observado es:

```text
                Bytecode
                   │
                   ▼
            Ejecución inicial
                   │
                   ▼
          Recopilación de datos
                   │
                   ▼
              JIT Compiler
                   │
                   ▼
        Código nativo optimizado
                   │
                   ▼
          Ejecuciones posteriores
```

Por eso, cuando un mismo fragmento de código se ejecuta repetidamente dentro del mismo proceso JVM, sus tiempos pueden cambiar.

### Importante

El resultado de un benchmark pequeño no debe interpretarse como una medición científica definitiva del rendimiento de Java.

Factores como:

* compilación JIT,
* optimizaciones,
* garbage collection,
* planificación de hilos del sistema operativo,
* frecuencia del procesador,
* carga del sistema,
* y otras características del entorno

pueden afectar las mediciones.

Para microbenchmarks rigurosos de JVM se utilizan herramientas especializadas como **JMH (Java Microbenchmark Harness)**.

---

# 🧠 Pregunta Senior JVM Engineer

### Pregunta

Si el bytecode proporciona portabilidad entre distintas JVM y dos implementaciones diferentes pueden ejecutar exactamente el mismo `.class`:

> ¿En qué capa de la arquitectura reside la posibilidad de que ese mismo bytecode tenga un perfil de latencia y consumo de memoria completamente distinto?

### Respuesta

La divergencia puede originarse en los **subsistemas internos de la implementación de la JVM**, no en el lenguaje Java ni en el bytecode en sí.

Dos subsistemas importantes son:

### JIT Compiler

Diferentes JVM pueden utilizar estrategias distintas para:

* detectar código caliente,
* compilar bytecode a código nativo,
* realizar inline,
* eliminar código,
* optimizar ramas,
* y aplicar otras optimizaciones.

### Garbage Collector

Las JVM también pueden utilizar diferentes algoritmos y políticas para administrar el heap, afectando:

* pausas,
* throughput,
* uso de memoria,
* frecuencia de recolección,
* y comportamiento de la aplicación bajo carga.

Por lo tanto:

```text
Mismo bytecode
      │
      ├──────────────► JVM A
      │                 │
      │                 ├── JIT
      │                 ├── GC
      │                 └── Runtime
      │
      └──────────────► JVM B
                        │
                        ├── JIT
                        ├── GC
                        └── Runtime
```

Puede producir:

```text
Mismo bytecode
      ≠
Mismas instrucciones nativas
      ≠
Mismo rendimiento
      ≠
Mismo consumo de memoria
```

La portabilidad de Java garantiza que el bytecode pueda ser ejecutado por implementaciones compatibles de la JVM; **no garantiza que todas esas implementaciones produzcan el mismo perfil de rendimiento**.

---

# 📌 Conceptos consolidados

Con este reto se trabajaron los siguientes conceptos:

* JVM Runtime
* Bytecode
* JVM implementation
* HotSpot
* JIT Compiler
* JIT warm-up
* Código caliente
* Código nativo
* `Runtime.version()`
* `java.vm.name`
* `java.home`
* `ProcessHandle`
* PID de procesos Java
* `Runtime.availableProcessors()`
* `System.nanoTime()`
* Benchmark básico
* Observabilidad de procesos
* `jps -l`
* Garbage Collector
* Diferencias entre implementaciones de JVM

---

# 🏗️ Relación con Platform Engineering

Este reto representa una primera transición desde **programar en Java** hacia **entender el runtime que ejecuta Java**.

En Platform Engineering, conocer solamente el código de una aplicación no es suficiente.

También es necesario poder responder preguntas como:

```text
¿Qué JVM está ejecutando la aplicación?
        │
        ▼
¿Qué versión utiliza?
        │
        ▼
¿Qué proceso la representa?
        │
        ▼
¿Qué recursos detecta la JVM?
        │
        ▼
¿Cómo está ejecutándose el código?
        │
        ▼
¿Cómo está afectando el runtime al rendimiento?
```

Estas preguntas forman la base para posteriormente trabajar con herramientas como:

```text
jps
jstat
jcmd
JFR
JMH
```

y avanzar hacia diagnóstico, observabilidad y tuning de aplicaciones JVM.

---

# 🗺️ Mapa del reto

```text
                CHAPTER 1
             JVM ARCHITECTURE
                    │
                    ▼
          ┌───────────────────┐
          │ Identidad Runtime  │
          └─────────┬─────────┘
                    │
          ┌─────────▼─────────┐
          │ Runtime.version() │
          │ java.vm.name      │
          │ java.home         │
          │ ProcessHandle PID │
          │ availableCPU      │
          └─────────┬─────────┘
                    │
                    ▼
              Observabilidad
                    │
                    ▼
                 jps -l
                    │
                    ▼
          ┌───────────────────┐
          │ Benchmark JIT     │
          └─────────┬─────────┘
                    │
                    ▼
             Ejecuciones
             repetidas
                    │
                    ▼
               Warm-up
                    │
                    ▼
              JIT Compiler
                    │
                    ▼
          Código nativo optimizado
```

---

## 📊 Estado del capítulo

| Elemento                          | Estado       |
| --------------------------------- | ------------ |
| Capítulo 1                        | ✅ Completado |
| Retos                             | 1 / 1        |
| Identidad del Runtime             | ✅            |
| PID y `jps -l`                    | ✅            |
| Benchmark                         | ✅            |
| Warm-up del JIT                   | ✅            |
| Análisis JVM                      | ✅            |
| Relación con Platform Engineering | ✅            |

---

## 🎓 Aprendizaje principal

El aprendizaje central de este capítulo es que **Java no debe entenderse únicamente como un lenguaje de programación**.

El mismo bytecode puede ser ejecutado por diferentes implementaciones de la JVM, y cada implementación puede tomar decisiones distintas sobre compilación, optimización y administración de memoria.

Por ello, para trabajar con Java a nivel de **Platform Engineering / JVM Engineering**, es necesario analizar las tres capas:

```text
Código Java
     │
     ▼
Bytecode
     │
     ▼
JVM Runtime
     │
     ├── JIT Compiler
     ├── Garbage Collector
     ├── Memory Management
     └── Runtime / OS interaction
```

Este capítulo establece la base para los siguientes experimentos de memoria, objetos, clases, herencia, rendimiento y concurrencia.
