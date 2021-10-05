CC=gcc
SDIR=src
ODIR=obj
BDIR=bin

MAIN=example/dummy_ai.c
EXE=$(BDIR)/dummy_ai
SRC=$(wildcard $(SDIR)/*.c)
OBJ=$(SRC:$(SDIR)/%.c=$(ODIR)/%.o)

.PHONY: all clean

all: $(EXE)

$(EXE): $(OBJ) | $(BDIR)
	$(CC) $(MAIN) $^ -o $@

$(ODIR)/%.o: $(SDIR)/%.c | $(ODIR)
	$(CC) -c $< -o $@

$(BDIR) $(ODIR):
	mkdir -p $@

clean:
	@$(RM) -rv $(BDIR) $(ODIR)
