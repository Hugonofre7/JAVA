# Chapter 7 — Exceptions & Logging

📁 **Carpeta:** `07-exceptions-logging/`
📄 **Archivo:** `ExceptionsAndLogging.java`
🎯 **Retos totales del capítulo:** 3

---

## 🎯 Objetivo del capítulo

Este capítulo estudia el manejo de excepciones en Java desde una perspectiva orientada a JVM y Systems Engineering, incluyendo:

* Stack Unwinding
* `try`
* `catch`
* `finally`
* Rutas normales y excepcionales de salida
* Excepciones checked
* Excepciones unchecked
* Excepciones de dominio
* Exception Chaining
* Causa raíz
* `Throwable`
* `try-with-resources`
* `IOException`
* `FileWriter`
* Preservación del Stack Trace
* Retry Policies
* Reintentos ante fallos
* Integración entre excepciones y resiliencia

Los retos combinan el manejo de excepciones con mecanismos fundamentales para construir aplicaciones capaces de diagnosticar fallos, preservar la causa raíz de los errores y aplicar estrategias de recuperación mediante reintentos.

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals, 12th Edition* — Cay S. Horstmann

El capítulo se complementa con conceptos de JVM relacionados con stack unwinding, ejecución de `finally`, propagación de excepciones y preservación de la cadena de causalidad.

---

# 🧪 Reto 1 — Stack unwinding real: rastreo del `finally` en múltiples rutas de salida

## 🎯 Objetivo

Comprender qué ocurre con un bloque `finally` cuando un método abandona un bloque `try` mediante diferentes rutas de ejecución.

Se implementa un método que genera diferentes tipos de salida dependiendo del valor recibido:

```java
static int riskyOperation(int input) {
    try {
        if (input < 0) {
            throw new IllegalArgumentException(
                "input negativo: " + input
            );
        }

        if (input == 0) {
            throw new ArithmeticException(
                "división por cero simulada"
            );
        }

        return 100 / input;
    } finally {
        System.out.println(
            "finally ejecutado para input=" + input
        );
    }
}
```

El método se prueba con tres valores:

```text
input = -5
input = 0
input = 10
```

Cada llamada se encuentra dentro de su propio `try/catch`, permitiendo observar el orden real de ejecución entre `finally` y `catch`.

---

# 🔮 Pregunta de predicción

> Para el caso `input = -5`, que lanza la excepción antes de llegar al `return`, ¿se espera que la línea del `finally` se imprima antes o después del mensaje que se imprime en el `catch` correspondiente?

### Predicción

El `finally` se ejecuta **antes** de que la excepción pueda continuar propagándose hacia el `catch` del `main`.

La ejecución abandona el bloque `try` debido a la excepción.

Antes de continuar con el proceso de propagación, Java ejecuta el bloque `finally`.

Conceptualmente:

```text
riskyOperation(-5)
        │
        ▼
      try
        │
        ▼
IllegalArgumentException
        │
        ▼
     finally
        │
        ▼
propagación de excepción
        │
        ▼
catch del main
```

Por lo tanto, el mensaje:

```text
finally ejecutado para input=-5
```

aparece antes del mensaje producido por el `catch`.

---

## 🧠 Stack Unwinding

Cuando una excepción no es manejada inmediatamente dentro del método donde ocurre, la JVM comienza un proceso conocido como **stack unwinding**.

Conceptualmente:

```text
main()
 │
 │ llama
 ▼
riskyOperation()
 │
 │ excepción
 ▼
finally
 │
 │ propagación
 ▼
main() catch
```

La JVM abandona progresivamente los frames de la pila hasta encontrar un manejador compatible con la excepción.

Durante este proceso, los bloques `finally` correspondientes deben ejecutarse.

---

## 🧠 `finally` y rutas de salida

El bloque `finally` está asociado a las diferentes formas en que el flujo puede abandonar el `try`.

En términos conceptuales:

```text
                  try
                   │
          ┌────────┼─────────┐
          │        │         │
          ▼        ▼         ▼
       return   excepción   ejecución
          │        │         │ normal
          └────────┼─────────┘
                   │
                   ▼
                finally
                   │
                   ▼
              continuación
```

Esto permite garantizar que determinadas operaciones de limpieza o finalización se ejecuten independientemente de cómo termine el bloque protegido.

---

# 🧠 Conceptos consolidados — Reto 1

* Stack Unwinding
* Stack Frames
* Propagación de excepciones
* `try`
* `catch`
* `finally`
* `IllegalArgumentException`
* `ArithmeticException`
* Rutas excepcionales de salida
* Ejecución garantizada de `finally`

---

# 🧪 Reto 2 — Excepciones de dominio con chaining + try-with-resources

## 🎯 Objetivo

Comprender cómo una aplicación puede transformar una excepción técnica de bajo nivel en una excepción de dominio sin perder la causa original del problema.

Se define una excepción checked propia:

```java
class LogWriteException extends Exception {

    LogWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

El método de escritura utiliza `try-with-resources`:

```java
static void writeLogEntry(
        String path,
        String entry
) throws LogWriteException {
    try (FileWriter writer = new FileWriter(path, true)) {
        writer.write(entry + "\n");
    } catch (IOException e) {
        throw new LogWriteException(
            "No se pudo escribir el log",
            e
        );
    }
}
```

La primera escritura utiliza una ruta válida y la segunda una ruta deliberadamente inválida.

Cuando ocurre el error, se muestran tanto la excepción de dominio como su causa:

```java
System.out.println(e.getMessage());
System.out.println(e.getCause().getMessage());
```

---

# 🔮 Pregunta de predicción

> Cuando una `IOException` se envuelve dentro de `LogWriteException` utilizando `super(message, cause)`, ¿qué se perdería si se utilizara `null` como causa?

### Predicción

Se perdería la excepción original:

```text
IOException
```

y con ella información importante para diagnosticar el fallo real.

La excepción original contiene información como:

* Mensaje concreto del sistema
* Ruta involucrada
* Contexto del fallo de I/O
* Información relacionada con permisos o acceso
* Stack trace original
* Punto donde ocurrió realmente el error

La cadena correcta conserva esa información:

```text
LogWriteException
       │
       │ cause
       ▼
   IOException
       │
       ▼
  error original
```

Mientras que utilizar:

```java
throw new LogWriteException(
    "fallo de escritura",
    null
);
```

rompería la cadena de causalidad.

---

## 🧠 Exception Chaining

El **exception chaining** permite representar diferentes niveles de abstracción del mismo fallo.

En este caso:

```text
Application
     │
     ▼
LogWriteException
     │
     │ cause
     ▼
IOException
     │
     ▼
Operating System / File System
```

La aplicación puede trabajar con una excepción de dominio mientras conserva la información técnica necesaria para diagnosticar el problema.

Esto es especialmente importante en sistemas de producción.

---

## 🧠 Causa raíz

La propiedad:

```java
e.getCause()
```

permite acceder a la excepción que originó el problema.

Por ejemplo:

```java
catch (LogWriteException e) {
    System.out.println(e.getMessage());
    System.out.println(e.getCause().getMessage());
}
```

La diferencia entre ambas informaciones es importante:

```text
e.getMessage()
       │
       ▼
¿Qué significa el fallo para mi aplicación?

e.getCause()
       │
       ▼
¿Qué ocurrió originalmente?
```

La primera representa el contexto de dominio.

La segunda preserva el origen técnico.

---

## 🧠 `try-with-resources`

El `try-with-resources` permite declarar recursos que deben cerrarse automáticamente.

Conceptualmente:

```text
try-with-resources
        │
        ▼
   abrir recurso
        │
        ▼
   escribir log
        │
        ▼
 cerrar recurso
        │
        ▼
 continuar / propagar excepción
```

En este reto, `FileWriter` implementa `AutoCloseable`, por lo que Java administra automáticamente su cierre.

Esto reduce el riesgo de dejar recursos abiertos cuando ocurre una excepción.

---

# 🧠 Conceptos consolidados — Reto 2

* Checked Exceptions
* Excepciones de dominio
* `LogWriteException`
* `IOException`
* Exception Chaining
* `Throwable`
* `getCause()`
* Preservación de la causa raíz
* Stack Trace
* `try-with-resources`
* `AutoCloseable`
* Gestión automática de recursos

---

# 🧪 Reto 3 — Integrando `RetryPolicy` con excepciones: reintento ante fallos de escritura

## 🎯 Objetivo

Integrar la `RetryPolicy` desarrollada en el Capítulo 6 con el sistema de excepciones del Capítulo 7 para construir un mecanismo real de reintentos ante fallos de escritura.

La política utilizada es:

```java
RetryPolicy maxThreeRetries =
        attempt -> attempt < 3;
```

El método `writeLogWithRetry` intenta escribir el registro y, cuando ocurre un `LogWriteException`, analiza el fallo y consulta la política para determinar si debe volver a intentar.

Conceptualmente:

```text
writeLogWithRetry()
        │
        ▼
  intento de escritura
        │
        ├─────────────── éxito
        │                  │
        │                  ▼
        │                return
        │
        ▼
LogWriteException
        │
        ▼
consultar RetryPolicy
        │
        ├── retry = true ──► nuevo intento
        │
        └── retry = false ─► abandonar
```

La ruta utilizada deliberadamente no existe:

```text
/ruta/que/no/existe/app.log
```

Por lo tanto, todos los intentos producen un fallo.

---

# 🔮 Pregunta de predicción

> Con `maxThreeRetries = attempt -> attempt < 3`, y sabiendo que la ruta va a fallar siempre, ¿cuántos intentos totales se esperan antes de abandonar definitivamente, contando desde `attempt = 0`?

### Predicción

Se esperan **4 intentos totales**.

La secuencia es:

```text
attempt = 0
     │
     ▼
fallo → retry = true

attempt = 1
     │
     ▼
fallo → retry = true

attempt = 2
     │
     ▼
fallo → retry = true

attempt = 3
     │
     ▼
fallo → retry = false
     │
     ▼
abandonar
```

La condición:

```java
attempt < 3
```

permite reintentar para los valores `0`, `1` y `2`.

Cuando `attempt == 3`, la política devuelve `false`.

Por lo tanto:

```text
3 reintentos
+
1 intento inicial
=
4 intentos totales
```

---

## 🧠 Retry Policy

La `RetryPolicy` separa la decisión de reintentar de la lógica que ejecuta la operación.

Conceptualmente:

```text
              write operation
                     │
                     ▼
                exception
                     │
                     ▼
               RetryPolicy
                     │
              ┌──────┴──────┐
              │             │
             true          false
              │             │
              ▼             ▼
          retry again      stop
```

Esto permite cambiar la estrategia sin modificar directamente la lógica de escritura.

Por ejemplo, una política podría utilizar:

```text
Número máximo de intentos
        │
        ▼
     3 retries
```

Mientras otra podría implementar:

```text
Backoff
   │
   ▼
1s → 2s → 4s
```

La separación entre **operación** y **política de recuperación** es un principio importante en sistemas resilientes.

---

## 🧠 Integración entre Capítulo 6 y Capítulo 7

Este reto conecta directamente los conceptos estudiados en ambos capítulos:

```text
Chapter 6
Interfaces & Lambdas
        │
        ▼
    RetryPolicy
        │
        │
        ▼
Chapter 7
Exceptions & Logging
        │
        ▼
LogWriteException
        │
        ▼
Retry mechanism
```

La lambda proporciona el comportamiento de la política:

```java
attempt -> attempt < 3
```

Mientras la excepción proporciona la señal de que la operación falló:

```text
LogWriteException
        │
        ▼
RetryPolicy
        │
        ▼
¿Reintentar?
```

Este patrón constituye la base conceptual del proyecto posterior:

```text
resilient_log_writer.java
```

---

# 🧠 Conceptos consolidados — Reto 3

* Reutilización de interfaces funcionales
* `RetryPolicy`
* Lambda expressions
* Retry mechanism
* Reintentos
* Intento inicial
* Número máximo de reintentos
* Separación entre operación y política
* Excepciones como señal de fallo
* Resiliencia
* Integración entre capítulos

---

# 🏗️ Relación con JVM y Systems Engineering

Las excepciones representan un mecanismo fundamental para comunicar condiciones anormales durante la ejecución de un programa.

Desde una perspectiva de JVM, una excepción implica cambios en el flujo normal de ejecución:

```text
Código normal
     │
     ▼
   método
     │
     ▼
  excepción
     │
     ▼
Stack Unwinding
     │
     ▼
finally
     │
     ▼
buscar handler
     │
     ▼
catch compatible
```

La JVM utiliza la información asociada a la excepción para determinar dónde existe un manejador compatible.

En un sistema real, este mecanismo puede combinarse con capas de abstracción:

```text
Operating System
       │
       ▼
   IOException
       │
       ▼
LogWriteException
       │
       ▼
 RetryPolicy
       │
       ▼
Recovery strategy
```

Esto permite separar diferentes responsabilidades:

```text
I/O layer
   │
   └── detecta fallo técnico

Domain layer
   │
   └── representa el fallo como excepción de dominio

Resilience layer
   │
   └── decide si reintentar

Application layer
   │
   └── registra / reporta el resultado
```

Esta separación es especialmente importante en Platform Engineering y sistemas distribuidos porque permite construir componentes capaces de detectar fallos, conservar información diagnóstica y aplicar estrategias de recuperación.

---

# 🗺️ Mapa del capítulo

```text
                    Chapter 7
                        │
          ┌─────────────┴─────────────┐
          │                           │
     Exceptions                    Logging
          │                           │
          ▼                           ▼
   Stack Unwinding               FileWriter
          │                           │
          ▼                           ▼
       finally                  try-with-resources
          │                           │
          ▼                           ▼
   Exception Handler             IOException
          │                           │
          └─────────────┬─────────────┘
                        │
                        ▼
                Exception Chaining
                        │
                        ▼
                 Domain Exception
                        │
                        ▼
                LogWriteException
                        │
                        ▼
                  RetryPolicy
                        │
                        ▼
                    Retries
                        │
                        ▼
                  Resilience
```

---

# 📌 Conceptos consolidados del capítulo

```text
Java Exception Handling
          │
          ├── try / catch / finally
          │       │
          │       └── Stack Unwinding
          │
          ├── Exceptions
          │       │
          │       ├── Checked
          │       └── Unchecked
          │
          ├── Exception Chaining
          │       │
          │       ├── Throwable
          │       ├── getCause()
          │       └── Root Cause
          │
          ├── Resource Management
          │       │
          │       └── try-with-resources
          │
          └── Resilience
                  │
                  ├── RetryPolicy
                  ├── Retry
                  └── Failure Recovery
```

---

# 🎓 Aprendizaje principal

Una excepción no representa únicamente un error que debe capturarse.

Desde una perspectiva de Systems Engineering, una excepción puede transportar información fundamental sobre un fallo y participar en una estrategia de recuperación.

El flujo completo estudiado en este capítulo puede representarse como:

```text
Failure
   │
   ▼
Exception
   │
   ▼
Stack Unwinding
   │
   ▼
finally
   │
   ▼
Exception Handler
   │
   ▼
Domain Exception
   │
   ▼
Root Cause
   │
   ▼
Retry Policy
   │
   ▼
Recovery
```

La idea fundamental es comprender que **un sistema robusto no solamente detecta que algo falló; también debe preservar la causa del fallo, liberar correctamente sus recursos y determinar si la operación puede recuperarse mediante una estrategia como un reintento**.

---

## 📊 Estado del capítulo

| Reto   | Tema principal                          | Estado       |
| ------ | --------------------------------------- | ------------ |
| Reto 1 | Stack Unwinding + `finally`             | ✅ Completado |
| Reto 2 | Exception Chaining + try-with-resources | ✅ Completado |
| Reto 3 | RetryPolicy + excepciones               | ✅ Completado |

**Capítulo 7 completado: 3 / 3 retos.**
