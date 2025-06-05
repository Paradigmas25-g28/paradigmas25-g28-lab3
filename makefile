# Makefile para el Laboratorio 3 de Paradigmas - FeedReader con Spark

# --- Configuración de Paths ---
JAVA_PATH=
SPARK_FOLDER=/home/tomas/spark-3.5.1-bin-hadoop3
OUT_DIR=out
LIB_DIR=lib

# --- Comandos ---
JAVAC=$(JAVA_PATH)javac
JAVA=$(JAVA_PATH)java

# --- Classpath ---
# Construcción del classpath para los JARs de Spark
SPARK_JARS_RAW=$(shell find $(SPARK_FOLDER)/jars -name "*.jar" 2>/dev/null | tr '\n' ':')
ifeq ($(suffix $(SPARK_JARS_RAW)),:)
  SPARK_JARS_CLEAN := $(patsubst %:,%,$(SPARK_JARS_RAW))
else
  SPARK_JARS_CLEAN := $(SPARK_JARS_RAW)
endif

# Classpath final
CLASSPATH=$(OUT_DIR):$(LIB_DIR)/json-20250107.jar:$(LIB_DIR)/fliptables-1.0.2.jar:$(SPARK_JARS_CLEAN)

# --- Archivos Fuente ---
SOURCE_PATH=src/main/java
SOURCES=$(shell find $(SOURCE_PATH) -name "*.java")

# --- Clase Principal ---
MAIN_CLASS=FeedReaderMain

# --- Argumentos para la ejecución (opcional) ---
# ARGS=config/subscriptions_large.json # Para el archivo grande
ARGS=config/subscriptions.json # Para el archivo por defecto

# --- Argumentos JVM para Java 9+ ---
JAVA_OPTS=--add-opens java.base/sun.nio.ch=ALL-UNNAMED \
          --add-opens java.base/java.nio=ALL-UNNAMED \
          --add-opens java.base/java.lang=ALL-UNNAMED \
          --add-opens java.base/java.lang.invoke=ALL-UNNAMED \
          --add-opens java.base/java.util=ALL-UNNAMED \
          --add-opens java.base/sun.util.calendar=ALL-UNNAMED \
          --add-opens java.base/sun.security.action=ALL-UNNAMED \
          --add-opens java.base/java.net=ALL-UNNAMED

# --- Targets ---

all: build run

build:
	@echo "Creando directorio de salida $(OUT_DIR)..."
	@mkdir -p $(OUT_DIR)
	@echo "Compilando fuentes Java..."
	$(JAVAC) -Xlint:-options -cp "$(CLASSPATH)" -d $(OUT_DIR) $(SOURCES) # Añadido -Xlint:-options
	@echo "Compilación completada."

run: build
	@echo "Ejecutando $(MAIN_CLASS) con Spark..."
	$(JAVA) $(JAVA_OPTS) -cp "$(CLASSPATH)" $(MAIN_CLASS) $(ARGS)
	@echo "Ejecución finalizada."

clean:
	@echo "Limpiando directorio de salida $(OUT_DIR)..."
	rm -rf $(OUT_DIR)
	@echo "Limpieza completada."

help:
	@echo "Targets disponibles:"
	@echo "  all    : Compila y ejecuta el programa (target por defecto)."
	@echo "  build  : Compila el programa."
	@echo "  run    : Ejecuta el programa (requiere compilación previa)."
	@echo "  clean  : Elimina los archivos compilados."
	@echo ""
	@echo "Variables configurables:"
	@echo "  SPARK_FOLDER : Ruta al directorio de instalación de Spark."
	@echo "  JAVA_PATH    : (Opcional) Ruta al directorio bin de una JDK específica."
	@echo "  LIB_DIR      : Directorio para JARs de dependencias (json, fliptables)."
	@echo "  MAIN_CLASS   : Clase principal a ejecutar."
	@echo "  ARGS         : Argumentos para pasar a la clase principal."

.PHONY: all build run clean help