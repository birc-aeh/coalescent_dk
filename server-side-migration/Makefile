
CC = gcc
CFLAGS = -O0 -g -Wall

HEADERS = structures.h memory.h sequence.h tree.h terminator.h arguments.h
OBJS = main.o sequence.o tree.o terminator.o arguments.o
TARGET = migrate

all: $(HEADERS) $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(TARGET) -lm
	chmod 755 $(TARGET)

clean:
	rm -f $(OBJS) $(HOGOBJS) *~

depend:
	depend $(OBJS)


# DO NOT ERASE THIS COMMENT
terminator.o: structures.h terminator.h memory.h tree.h 
tree.o: tree.h memory.h structures.h sequence.h terminator.h 
sequence.o: structures.h sequence.h memory.h 
main.o: structures.h tree.h sequence.h memory.h 
