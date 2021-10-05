all: *.c
	gcc -o dummy_ai *.c
print: *.c
	gcc -o dummy_ai *.c -D PRINT
clean:
	rm dummy_ai
