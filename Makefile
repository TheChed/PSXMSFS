CC = x86_64-w64-mingw32-g++
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic
VER=$(shell date +"%y%m%d%H%M")
LINK= -LInclude -lwsock32 -lSimConnect

DEPS = PSXMSFS.h util.h update.h MSFS.h
OBJ = link.o PSXMSFS.o MSFS.o PSX.o connect.o util.o update.o
LIBOBJ = MSFS.o PSX.o connect.o util.o update.o PSXMSFS.o

all: LINK win exe
rel: all hash

comp: PSXMSFS win

lib: $(LIBOBJ)
	$(CC) $(LIBOBJ) -shared -fPIC -o PSXMSFS.dll $(LINK)

LINK: $(OBJ) 
	$(CC) $(OBJ) -o LINK $(LINK)
	cp LINK.exe LINK

%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -D__MINGW__ -DVER=$(VER) -DCOMP="\"MINGW\"" -c $<

clean:
	rm -rf PSXMSFS *.o *.exe *.dll

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

win:
	cp --update=none *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
	cp --update=none *.h /home/stephan/NAS/TRANSFERT/pfpx/src
exe:
	cp PSXMSFS.exe /home/stephan/NAS/TRANSFERT/pfpx
