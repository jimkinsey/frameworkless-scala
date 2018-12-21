SRC = .
BIN = ./bin
LIB = ./lib
DIST = ./dist
SOURCES = $(shell ls $(SRC)/**/*.scala)
S = scala
SC = scalac

compile:
	@mkdir -p $(BIN)
	@$(SC) -cp $(BIN) -d $(BIN) $(SOURCES)

clean:
	@$(RM) -r $(BIN) $(LIB) $(DIST)
	
run:
	@$(S) -cp $(BIN) todo.App

test:
	@$(S) -cp $(BIN) todo.TestRunner

assemble:
	@mkdir -p $(DIST)
	@cp -r $(BIN)/* $(DIST)/
	@unzip $(LIB)/* -d $(DIST)/
	cd $(DIST); jar cef todo.App todo.jar *

deps:
	@mkdir -p $(LIB)
	@wget https://scala-ci.typesafe.com/artifactory/sonatype-releases/org/scala-lang/scala-library/2.12.7/scala-library-2.12.7.jar -O ./lib/scala-library-2.12.7.jar

