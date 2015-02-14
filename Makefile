
CC = gcc
CFLAGS = -O3 -Wall -m32
#CFLAGS = -g -Wall

HEADERS = structures.h sets.h memory.h sequence.h tree.h terminator.h arguments.h 
OBJS = main.o sets.o memory.o sequence.o tree.o terminator.o arguments.o 
TARGET = hudson

all: $(HEADERS) $(OBJS)
	$(CC) $(CFLAGS) $(OBJS) -o $(TARGET) -lm
	chmod 755 $(TARGET)

clean:
	rm -f $(OBJS) $(HOGOBJS) *~

depend:
	depend $(OBJS)


# DO NOT ERASE THIS COMMENT
terminator.o: structures.h terminator.h memory.h tree.h 
tree.o: sets.h tree.h memory.h structures.h sequence.h terminator.h 
sequence.o: structures.h sequence.h memory.h 
memory.o: memory.h 
sets.o: structures.h sets.h memory.h terminator.h 
main.o: structures.h tree.h sequence.h sets.h memory.h 
