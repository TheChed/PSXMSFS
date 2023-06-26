CC = x86_64-w64-mingw32-g++
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic
VER=$(shell date +"%y%m%d%H%M")
LINK= -LInclude -lwsock32 -lSimConnect

DEPS = PSXMSFSLIB.h util.h update.h MSFS.h
OBJ = link.o PSXMSFS.o MSFS.o PSX.o connect.o util.o update.o
LIBOBJ = MSFS.o PSX.o connect.o util.o update.o PSXMSFS.o
OBJLINK = link.o

all: lib link win exe
rel: all hash

comp: lib win

lib: $(LIBOBJ)
	$(CC) $(LIBOBJ) -shared -fPIC -o PSXMSFS.dll $(LINK)

link: $(OBJLINK) 
	$(CC) $(OBJLINK) -o LINK -lPSXMSFS $(LINK)
	cp LINK.exe LINK

%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -D__MINGW__ -DVER=$(VER) -DCOMP="\"MINGW\"" -c $<

clean:
	rm -rf LINK *.o *.exe *.dll

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

win:
	cp  *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
	cp  *.h /home/stephan/NAS/TRANSFERT/pfpx/src
exe:
	cp PSXMSFS.lib /home/stephan/Documents/C/src/PSX/Include
	cp LINK.exe /home/stephan/NAS/TRANSFERT/pfpx
