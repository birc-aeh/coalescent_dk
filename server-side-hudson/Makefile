
CC = gcc
CFLAGS = -O3 -Wall -m32
#CFLAGS = -g -Wall

HEADERS = structures.h sets.h sequence.h tree.h terminator.h arguments.h 
OBJS = main.o sets.o sequence.o tree.o terminator.o arguments.o 
TARGET = hudson

all: $(HEADERS) $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(TARGET) -lm
	chmod 755 $(TARGET)

clean:
	rm -f $(OBJS) $(HOGOBJS) *~

test: $(HEADERS) $(OBJS)
	$(CC) $(CFLAGS) -std=gnu99 test/test_sets.c -o runtests sets.o
	./runtests


# DO NOT ERASE THIS COMMENT
terminator.o: structures.h terminator.h tree.h 
tree.o: sets.h tree.h structures.h sequence.h terminator.h 
sequence.o: structures.h sequence.h 
sets.o: structures.h sets.h terminator.h 
main.o: structures.h tree.h sequence.h sets.h
