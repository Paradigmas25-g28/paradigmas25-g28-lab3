# Laboratorio 3 - Frameworks
## Computación Distribuida con Apache Spark

Este proyecto utiliza el framework Apache Spark para procesar y analizar noticias de diversas fuentes (feeds RSS y Reddit). La aplicación descarga artículos, extrae entidades nombradas utilizando diferentes heurísticas y realiza un conteo distribuido de las mismas, agrupándolas por entidad, categoría y tema.

---

## Configuración del entorno y ejecución

### Prerrequisitos
- **Java Development Kit (JDK) 11:** El proyecto está configurado para compilar y ejecutarse con Java 11.
- **Apache Maven:** Necesario para gestionar las dependencias y ejecutar el proyecto.
- **Spark 3.5.1**  

### Instrucciones de Ejecución

  **Ejecutar la aplicación con el Makefile:**
    El `Makefile` proporcionado simplifica la ejecución. Utiliza el plugin `exec:java` de Maven para compilar y correr la aplicación directamente, pasando los argumentos necesarios.

    Existen tres modos de ejecución, controlados por la variable `HEURISTIC`:

    *   **Modo por defecto (Conteo por Diccionario):**
        No se especifica ninguna heurística. La detección de entidades se basa en un diccionario estático predefinido en la clase `Heuristic`.
        ```bash
        make run
        ```

    *   **Modo QuickHeuristic (`-qh`):**
        Utiliza una heurística simple que identifica como entidad cualquier palabra que comience con mayúscula y no sea una "stop word" común.
        ```bash
        make run HEURISTIC="-qh"
        ```

    *   **Modo RandomHeuristic (`-rh`):**
        Utiliza una heurística que clasifica de manera aleatoria si una palabra es o no una entidad. Útil para pruebas y comparación.
        ```bash
        make run HEURISTIC="-rh"
        ```

    También es posible especificar una ruta diferente para el archivo de suscripciones:
    ```bash
    make run HEURISTIC="config/otras_suscripciones.json -qh"
    ```

### Resultado Esperado
Tras la ejecución, la terminal mostrará primero los logs de Maven y Spark. Luego, aparecerá la configuración utilizada y, finalmente, se imprimirán tres tablas con los resultados del análisis:
1.  **Conteo Final de Entidades:** Lista de todas las entidades nombradas encontradas y su frecuencia total.
2.  **Conteo Final por Categoría:** Frecuencia total de cada categoría (ej. `Person`, `Place`, `Company`).
3.  **Conteo Final por Tema:** Frecuencia total de cada tema (ej. `Football`, `Politics`, `Technology`).

El contenido de estas tablas variará significativamente dependiendo de la heurística seleccionada.

---

## Decisiones de diseño

-   **Inyección de Heurística en Tiempo de Ejecución:** El `main` de la aplicación determina qué heurística usar basándose en los argumentos de la línea de comandos. Esta elección se pasa a la lógica de procesamiento de Spark, permitiendo que el mismo pipeline de datos se comporte de manera diferente sin cambiar su estructura.

-   **Clases de Datos Serializables:** Todas las clases que modelan datos (ej. `Article`, `Feed`, `NamedEntity`, `SingleSubscription`) implementan la interfaz `java.io.Serializable`. Esto es un requisito fundamental de Spark, ya que necesita poder enviar objetos a través de la red hacia los nodos trabajadores (executors).

-   **Lógica de Descarga y Parseo dentro de Transformaciones:** La descarga de feeds y el parseo de su contenido se realizan dentro de una transformación `flatMap`. Esto asegura que el trabajo de I/O y procesamiento se distribuya y ejecute en paralelo en los workers de Spark, en lugar de ser un cuello de botella en el nodo principal (driver).

---

## Conceptos importantes

La aplicación sigue un flujo de procesamiento de datos distribuido orquestado por Spark:

1.  **Inicio y Configuración:** El método `main` parsea los argumentos para determinar la ruta del archivo de suscripciones y la heurística a utilizar.
2.  **Creación del Contexto Spark:** Se inicializa una `SparkSession` y un `JavaSparkContext`, que son el punto de entrada a todas las funcionalidades de Spark.
3.  **Lectura y Paralelización:** El `SubscriptionParser` lee el archivo `subscriptions.json`. La lista de suscripciones (`List<SingleSubscription>`) se convierte en el primer RDD (Resilient Distributed Dataset) usando `sc.parallelize()`.
4.  **Descarga de Feeds (`flatMap`):** El RDD de suscripciones se transforma. Para cada `SingleSubscription`, la función `generateFeedsFromSubscription` descarga y parsea el contenido (RSS o Reddit), pudiendo generar múltiples `Feed`. Se usa `flatMap` porque una suscripción puede tener varios temas, resultando en varios feeds. El resultado es un `RDD<Feed>`.
5.  **Extracción de Artículos (`flatMap`):** El `RDD<Feed>` se "aplana" de nuevo para obtener un `RDD<Article>` que contiene todos los artículos de todos los feeds.
6.  **Detección de Entidades y Conteo (`flatMap`):** Este es el núcleo del procesamiento. Cada `Article` del RDD es procesado en paralelo.
    - Se aplica la heurística seleccionada (`DEFAULT`, `QUICK` o `RANDOM`) al texto del artículo.
    - Por cada entidad encontrada, se emiten múltiples tuplas clave-valor para contar la entidad, su categoría y su tema. Por ejemplo, `("ENTITY_Biden", "OCCURRENCE")`, `("CAT_Person", "COUNT")`, `("TOPIC_Politics", "COUNT")`.
7.  **Agregación (`reduceByKey`):** El RDD de tuplas se convierte en un `JavaPairRDD` de la forma `(Clave, 1)` y luego `reduceByKey` suma eficientemente todas las ocurrencias para cada clave única de manera distribuida.
8.  **Recolección y Presentación:** El método `collect()` trae los resultados (ya agregados) al programa principal (driver). Finalmente, el código organiza los datos en mapas y los imprime en tablas formateadas.
9.  **Cierre:** `spark.stop()` libera todos los recursos utilizados por la sesión de Spark.

#### 2. ¿Por qué se decide usar Apache Spark para este proyecto?
Apache Spark se utiliza para resolver la necesidad de **paralelismo y escalabilidad**. El problema implica dos cuellos de botella principales:
1.  **Operaciones de I/O:** Descargar múltiples feeds de noticias de internet es una tarea lenta si se hace de forma secuencial. Spark permite realizar estas descargas en paralelo.
2.  **Operaciones de CPU:** Procesar el texto de cientos o miles de artículos para extraer entidades es intensivo en CPU.

Spark permite distribuir ambas tareas a través de los múltiples núcleos de un procesador (`master("local[*]")`) o, sin cambiar el código, a través de un clúster de múltiples máquinas. Esto acelera drásticamente el tiempo total de ejecución en comparación con una solución secuencial basada en bucles `for`.

#### 3. Liste las principales ventajas y desventajas que encontró al utilizar Spark.
**Ventajas:**
-   **Paralelismo Simplificado:** Permite escribir código de procesamiento paralelo sin gestionar hilos, locks o concurrencia manualmente. La API de RDD abstrae esta complejidad.
-   **Escalabilidad:** El mismo código que funciona en una laptop puede ejecutarse en un clúster de cientos de nodos para procesar terabytes de datos.
-   **API Expresiva y Funcional:** El uso de transformaciones como `map`, `flatMap` y `filter` con expresiones lambda resulta en un código más conciso y declarativo.
-   **Tolerancia a Fallos:** Spark puede re-ejecutar automáticamente tareas que fallen en los nodos trabajadores, lo que hace que las aplicaciones largas sean más robustas.

**Desventajas:**
-   **Curva de Aprendizaje:** Requiere entender un nuevo paradigma de programación (RDDs, transformaciones lazy, acciones) y conceptos como driver, executors y serialización.
-   **Overhead de Inicio:** Iniciar una `SparkSession` tiene un costo de tiempo. Para tareas muy pequeñas y rápidas, puede ser más lento que un script simple debido a este overhead.
-   **Complejidad en la Depuración:** Cuando ocurre un error dentro de una tarea en un executor, el stack trace puede ser largo y complejo, dificultando la identificación de la causa raíz.
-   **Gestión de Dependencias:** Requiere una configuración cuidadosa del `pom.xml` para crear un "uber-JAR" (con `maven-shade-plugin`) o para gestionar las dependencias que ya están provistas por el clúster (`scope=provided`).

#### 4. ¿Cómo se aplica el concepto de inversión de control en este laboratorio?
La **inversión de control (Inversion of Control - IoC)** es un principio fundamental en el uso de frameworks como Spark. En este laboratorio, se manifiesta de la siguiente manera:

-   **Delegación del Flujo de Ejecución:** En lugar de que nuestro código controle el flujo principal (ej. `for(article : articles) { process(article); }`), nosotros **escribimos la lógica de procesamiento y se la entregamos al framework**.
-   **El Qué, no el Cómo:** Como desarrolladores, definimos *qué* queremos hacer con los datos a través de una cadena de transformaciones (el DAG - Grafo Acíclico Dirigido). Por ejemplo: `articlesRDD.flatMap(...).mapToPair(...).reduceByKey(...)`.
-   **Spark toma el control:** Es Spark, el framework, quien decide *cómo, cuándo y dónde* ejecutar esa lógica. Decide:
    - Cómo dividir los datos en particiones.
    - A qué núcleo o máquina enviar cada tarea.
    - Cómo gestionar el movimiento de datos entre etapas (shuffling).
    - Cómo reintentar una tarea si falla.

El desarrollador deja de controlar directamente el flujo de ejecución y se lo cede a Spark, quien lo orquesta de la manera más eficiente y robusta posible.

#### 5. ¿Considera que Spark requiere que el codigo original tenga una integración tight vs loose coupling?
Spark promueve y se beneficia enormemente de un **acoplamiento flexible (loose coupling)**.

La lógica de negocio principal, como las funciones que se pasan a `flatMap` o `map`, está desacoplada del motor de Spark. Por ejemplo, la clase `QuickHeuristic` no tiene idea de que está siendo ejecutada por Spark. Las clases de datos como `Article` o `Feed` son simples POJOs (Plain Old Java Objects) que solo necesitan cumplir con el contrato de ser `Serializable`. No heredan de clases de Spark ni dependen del framework.

Este bajo acoplamiento es una gran ventaja, ya que permite que la misma lógica de negocio pueda ser probada unitariamente fuera de un contexto de Spark o incluso reutilizada en otra aplicación que no use Spark con cambios mínimos.

#### 6. ¿El uso de Spark afectó la estructura de su código original?
Sí, el uso de Spark **afectó significativamente la estructura del código** en comparación con una implementación secuencial como la del Laboratorio 2.

1.  **De un Flujo Imperativo a uno Declarativo:** El código original usaba bucles `for` anidados para iterar sobre suscripciones, temas y artículos (un estilo imperativo: "primero haz esto, luego esto..."). Con Spark, la estructura cambia a una cadena de transformaciones sobre RDDs (un estilo declarativo: "el resultado final es un RDD que se obtiene aplicando estas funciones a los datos").

2.  **Necesidad de Serialización:** El cambio más directo y obligatorio fue hacer que todas las clases de datos (`Article`, `Feed`, `SingleSubscription`, etc.) implementaran `java.io.Serializable`. Esto no es necesario en una aplicación de un solo proceso, pero es crucial para Spark.

3.  **Refactorización a Lambdas y Lógica Funcional:** Métodos que antes se llamaban secuencialmente se tuvieron que encapsular en expresiones lambda para ser pasados como argumentos a las transformaciones de Spark (`flatMap`, `mapToPair`).

4.  **Gestión de Estado y Objetos:** En una aplicación secuencial, se puede crear un objeto (como un `httpRequester` o un `RssParser`) una sola vez y reutilizarlo. En Spark, para evitar problemas de serialización y asegurar que cada tarea sea independiente, estos objetos se deben crear *dentro* de la lambda que se ejecuta en el worker. Esto se puede ver en `generateFeedsFromSubscription`, donde `httpRequester` y los parsers se instancian localmente. Este es un cambio sutil pero muy importante en el diseño.