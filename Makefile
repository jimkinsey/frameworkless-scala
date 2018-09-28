SRC = .
BIN = ./bin
LIB = ./lib
SOURCES = $(shell ls $(SRC)/**/*.scala)
S = scala
SC = scalac

compile:
	@mkdir -p $(BIN)
	@$(SC) -cp $(BIN) -d $(BIN) $(SOURCES)

clean:
	@$(RM) -r $(BIN) $(LIB)
	
run:
	@$(S) -cp $(BIN) todo.App

test:
	@$(S) -cp $(BIN) todo.TestRunner

assemble:
	@unzip $(LIB)/* -d $(BIN)/
	cd $(BIN); jar cef todo.App todo.jar *

deps:
	@mkdir -p $(LIB)
	@wget https://scala-ci.typesafe.com/artifactory/sonatype-releases/org/scala-lang/scala-library/2.12.7/scala-library-2.12.7.jar -O ./lib/scala-library-2.12.7.jar

