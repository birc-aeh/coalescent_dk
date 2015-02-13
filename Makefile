
CC = gcc
CFLAGS = -O3 -Wall
#CFLAGS = -Wall -g

HEADERS = structures.h sequence.h tree.h arguments.h selection.h
OBJS = main.o sequence.o tree.o arguments.o selection.o
TARGET = hudson

all: $(HEADERS) $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(TARGET) -lm
	chmod 755 $(TARGET)

clean:
	rm -f $(OBJS) *~


# DO NOT ERASE THIS COMMENT
tree.o: tree.h structures.h sequence.h
sequence.o: structures.h sequence.h
main.o: structures.h tree.h sequence.h
