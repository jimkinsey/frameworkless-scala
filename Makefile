SRC = .
SOURCES = $(shell ls $(SRC)/*.scala)
S = scala
SC = scalac
CP = .

compile: $(SOURCES:.scala=.class)

%.class: %.scala
	@echo "Compiling $*.scala.."
	@$(SC) -cp $(CP) -d . $*.scala

clean:
	@$(RM) $(SRC)/*.class