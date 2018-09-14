SRC = .
BIN = ./bin
SOURCES = $(shell ls $(SRC)/**/*.scala)
S = scala
SC = scalac

compile:
	@mkdir -p $(BIN)
	@$(SC) -cp ./bin -d $(BIN) $(SOURCES)

clean:
	@$(RM) -r $(BIN)
	
run:
	@$(S) -cp ./bin todo.App

test:
	@$(S) -cp ./bin todo.TestRunner
