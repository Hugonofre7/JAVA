# Chapter 4 — Objects and Classes

📁 **Carpeta:** `04-objects-classes/`
📄 **Archivo:** `cloud_resource_model.java`
🎯 **Retos totales del capítulo:** 2

---

## 🎯 Objetivo del capítulo

Este capítulo estudia el modelo de objetos de Java desde una perspectiva orientada a **JVM, memoria y sistemas**.

Los ejercicios buscan comprender qué ocurre realmente cuando:

* una variable referencia un objeto;
* dos variables apuntan al mismo objeto;
* se crea una instancia completamente nueva;
* un objeto vive en el Heap;
* un arreglo almacena referencias a objetos;
* un arreglo almacena valores primitivos directamente;
* y aparece el overhead asociado a los objetos.

El objetivo principal es pasar del modelo conceptual:

```text
variable → objeto
```

a un modelo más cercano a la realidad de la JVM:

```text
Stack
  │
  ├── referencia ───────┐
  │                     │
  └── referencia ───────┤
                        ▼
                     Heap
                  ┌──────────┐
                  │ Object   │
                  │ fields   │
                  └──────────┘
```

---

## 📚 Referencia teórica

**Libro principal:**

> *Core Java, Volume I: Fundamentals — 12th Edition*
> Cay S. Horstmann

Los retos de este capítulo toman como base los conceptos de **objetos, clases, referencias, constructores y estado**, y los llevan hacia una perspectiva de administración de memoria y comportamiento de la JVM.

Los ejercicios no son una reproducción directa de los ejemplos del libro. Fueron diseñados como experimentos prácticos para estudiar el comportamiento de objetos en memoria.

---

# 🧪 Reto 1 — Referencias, aliasing y objetos independientes

## 🎯 Objetivo

Crear una clase `CloudNode` que represente un recurso de infraestructura:

```text
CloudNode
├── id
├── cpuCores
└── memoryGb
```

Posteriormente se crean tres variables:

```text
original
alias
copy
```

pero solamente dos objetos reales.

---

## 🧱 Modelo utilizado

El reto crea inicialmente:

```java
CloudNode original = new CloudNode(...);
```

Después:

```java
CloudNode alias = original;
```

Finalmente:

```java
CloudNode copy = new CloudNode(...);
```

La diferencia fundamental es:

```text
original ────────┐
                 │
alias ───────────┘
                 │
                 ▼
             CloudNode A


copy ───────────────► CloudNode B
```

`original` y `alias` contienen referencias al **mismo objeto**.

`copy`, en cambio, contiene una referencia a un **objeto diferente**.

---

## 🔮 Pregunta de predicción

Antes de ejecutar el programa se planteó:

> Después de modificar `alias.cpuCores = 32`, ¿`original.cpuCores` también será 32 o conservará su valor inicial?

### Predicción

La respuesta fue:

> Sí, porque ambas variables apuntan al mismo objeto en memoria. No se creó un objeto nuevo, sino un alias o acceso al mismo objeto.

---

## 🧠 Explicación Stack / He
