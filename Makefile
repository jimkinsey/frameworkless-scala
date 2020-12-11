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
	@unzip -f $(LIB)/* -d $(DIST)/
	@cd $(DIST); jar cef todo.App todo.jar *

deps:
	@mkdir -p $(LIB)
	@if [ ! -f ./lib/scala-library-2.13.0.jar ]; then wget https://repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.0/scala-library-2.13.0.jar -O ./lib/scala-library-2.13.0.jar; fi;

