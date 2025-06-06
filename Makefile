# Clase principal que se ejecutará
MAIN_CLASS=FeedReaderMain


# Ejemplos de uso:
#   make run -> Sin heurística (valor por defecto: "")
#   make run HEURISTIC="-qh" -> Usa QuickHeuristic
#   make run HEURISTIC="-rh" -> Usa RandomHeuristic
HEURISTIC ?= ""


JAVA_HOME ?= $(shell \
	java11_candidates="\
		$$(which java 2>/dev/null | xargs readlink -f | sed 's:/bin/java::') \
		/usr/lib/jvm/java-11-openjdk-amd64 \
		/usr/lib/jvm/java-11-openjdk \
		/usr/lib/jvm/java-11 \
		/usr/lib/jvm/jdk-11* \
		/opt/java/openjdk11 \
		$(HOME)/jdk/jdk-11.0.23+9 \
	"; \
	for dir in $$java11_candidates; do \
		if [ -x "$$dir/bin/java" ]; then \
			v=$$($$dir/bin/java -version 2>&1 | grep 'version "11' || true); \
			if [ -n "$$v" ]; then echo $$dir; exit 0; fi; \
		fi; \
	done; \
	echo "ERROR: No se encontró una instalación válida de Java 11." >&2; \
	exit 1; \
)


run:
	@echo "======================================================"
	@echo "Usando JAVA_HOME: $(JAVA_HOME)"
	@if [ -n "$(HEURISTIC)" ]; then \
		echo "Ejecutando con heurística: $(HEURISTIC)"; \
	else \
		echo "Ejecutando con la lógica por defecto (sin heurística)"; \
	fi
	@echo "======================================================"
	
	JAVA_HOME=$(JAVA_HOME) mvn clean compile exec:java \
		-Dexec.mainClass="$(MAIN_CLASS)" \
		-Dexec.args="$(HEURISTIC)" \
		--quiet -T 1C


.PHONY: run
