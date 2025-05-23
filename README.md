# Laboratorio 2: Programacion Orientada a Objetos

## Integrantes:
- Buteler Constanza
- Deco Lautaro Ezequiel
- Torres Tomas Fabian
- Ortiz Ulises Valentin 

## Objetivos del proyecto
En este laboratorio vamos a implementar un lector automático de feeds, como aplicación de consola, bajo el paradigma orientado a objetos, utilizando como lenguaje de programación Java.  
El usuario de nuestro lector establece, mediante un archivo en formato .json, los distinto sitios (ej: new york times, etc) y sus respectivos tópicos (sports, business, etc) de los cuales obtener los diferentes feeds a mostrar por pantalla en forma legible y amigable para el usuario.  
Además, se agrega una funcionalidad a nuestro lector para computar heurísticamente las entidades nombradas más mencionadas en la lista de feeds.  

## Desarrollo
Comenzar este proyecto nos fue muy complicado ya que no estabamos familiarizados con Java, ni con el paradigma orientado a objetos.  
Primero establecimos las bases del parser en la clase GeneralParser, y a medida que avanzamos en el proyecto decidimos reestructurarlo y creamos una subclase JsonParser, para no repetir implementaciones en SubscriptionParser y RedditParser.  
Este luego se relaciona con el httpRequester al momento de realizar el pedido del feed al servidor de noticias, tomando una url y devolviendo el contenido del feed. Para lograrlo utilizamos paquetes de java.io y java.net.  
Para organizar la jerarquia de clases de las entidades nombradas, utilizamos herencia en su gran mayoria, exceptuando las clases que derivarian de Person y Place, que decidimos que sean atributos de estas implementandolas como clases POJO. Asi cada persona tiene su propio ID y al mismo tiempo un Name (que contiene su forma canonica, alternativa y su origen), un LastName (que contiene su forma canonica y su origen), y un Title (que contiene su forma canonica y un booleano si es profesional o no). Lo mismo sucede con la clase Place y sus respectivos atributos.    
Luego para poder aplicar estos cambios en nuestro proyecto, hubo que modificar el archivo Heuristic, reemplazando el Map<String, String> por Map<String, NamedEntity>, con variables estaticas para facilitar la creacion de estas nuevas NamedEntity.  
Con el fin de imprimir de forma amigable la informacion al usuario, se creo un nuevo archivo llamado TableOfNamedEntity, el cual se encarga de mostrar por pantalla la cantidad de ocurrencias de ciertas entidades especificando su topico y categoria. Para hacer esto importamos la libreria FlipTable de Jack Wharton.  
Finalizamos nuestro laboratorio modificando el archivo FeedReaderMain, donde modularizamos en una nueva funcion llamada feedsFromSubscriptions que se encarga de parsear el JSON de suscripciones, descargar los feeds llamando a httprequester, parsearlos dependiendo del tipo de feed que sean (Rss o Reddit) y crear los objetos Feed para luego llamar a la heuristica seleccionada por el usuario (-ne -rh, -ne -qh, o sin heuristica) la cual se imprime en consola mostrando toda la información asociada.

## Conclusión 
En este laboratorio aprendimos y practicamos todo lo relacionado con la programación orientada a objetos, como la herencia, el polimorfismo y el encapsulamiento. También empezamos a usar un nuevo lenguaje de programación en la carrera, lo cual fue un reto al principio porque no conocíamos bien su sintaxis. Aun así, pudimos ver el potencial que tiene un lenguaje orientado a objetos y lo útil que es poder abstraer funcionalidades para organizarnos mejor y trabajar más cómodos en grupo.

## Observaciones
### Forma de ejecucion  
- `mvn compile exec:java -Dexec.mainClass=FeedReaderMain -Dexec.args="" --quiet -T 1C` (ver articulos)
- `mvn compile exec:java -Dexec.mainClass=FeedReaderMain -Dexec.args=" -ne -rh" --quiet -T 1C` (ver random heuristic)
- `mvn compile exec:java -Dexec.mainClass=FeedReaderMain -Dexec.args=" -ne -qh" --quiet -T 1C` (ver quick heuristic)



