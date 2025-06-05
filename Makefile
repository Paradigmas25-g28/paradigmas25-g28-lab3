# Configuración general
MAIN_CLASS=FeedReaderMain
JAR_NAME=lab-2-paradigmas-1.0.0.jar
TARGET=target/$(JAR_NAME)
SPARK_HOME ?= $(shell dirname $(shell which spark-submit))/..

# Rutas
MVN=mvn
SPARK_SUBMIT=$(SPARK_HOME)/bin/spark-submit

# Variables de entorno
JAVA_OPTS=-Duser.language=es -Duser.country=AR

# Comandos
all: clean package

package:
	$(MVN) clean package

run: package
	$(SPARK_SUBMIT) \
		--class $(MAIN_CLASS) \
		--master local[*] \
		--conf "spark.driver.extraJavaOptions=$(JAVA_OPTS)" \
		$(TARGET)

clean:
	$(MVN) clean
