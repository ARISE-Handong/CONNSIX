CC=gcc

SRC_DIR=src
OBJ_DIR=obj
LIB_DIR=lib
BIN_DIR=bin

PGM=dummy_ai/dummy_ai.c
EXE=$(BIN_DIR)/dummy_ai

SRC=$(wildcard $(SRC_DIR)/*.c)
OBJ=$(SRC:$(SRC_DIR)/%.c=$(OBJ_DIR)/%.o)
LIB=$(LIB_DIR)/libconnsix.a

ARFLAGS=-rv
LDLIBS=-L$(LIB_DIR) -lconnsix

.PHONY: all clean

all: $(LIB) $(EXE)

$(EXE): $(PGM) | $(LIB) $(BIN_DIR)
	$(CC) $^ -o $@ $(LDLIBS)

$(LIB): $(OBJ) | $(LIB_DIR)
	ar $(ARFLAGS) $@ $^

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c | $(OBJ_DIR)
	$(CC) -c $< -o $@

$(OBJ_DIR) $(LIB_DIR) $(BIN_DIR):
	mkdir -p $@

clean:
	@$(RM) -rv $(OBJ_DIR) $(LIB_DIR) $(BIN_DIR)
