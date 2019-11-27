CC ?= gcc
CFLAGS = -Os -lm

default:
	# cd felsenstein_gui/ && make && mv felsenstein.jar ../html/ && make clean && cd ..
	# cd hudson_gui/ && make && mv hudson.jar ../html/ && make clean && cd ..
	mkdir -p bin
	$(CC) $(CFLAGS) server-side-migration/*.c -o bin/migration
	$(CC) $(CFLAGS) server-side-selection/*.c -o bin/selection
	$(CC) $(CFLAGS) server-side-hudson/*.c -o bin/hudson
	$(CC) $(CFLAGS) dumb-init/dumb-init.c -o bin/dumb-init
	strip bin/*
