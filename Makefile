SRC = .
BIN = ./bin
SOURCES = $(shell ls $(SRC)/**/*.scala)
S = scala
SC = scalac

compile:
	@mkdir -p $(BIN)
	@$(SC) -cp $(BIN) -d $(BIN) $(SOURCES)

clean:
	@$(RM) -r $(BIN)
	
run:
	@$(S) -cp $(BIN) todo.App

test:
	@$(S) -cp $(BIN) todo.TestRunner

assemble:
	cd $(BIN); jar cef todo.App todo.jar *
