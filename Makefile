CC = x86_64-w64-mingw32-g++
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic
VER=$(shell date +"%y%m%d%H%M")
LINK= -LInclude -lwsock32 -lSimConnect

DEPS = PSXMSFS.h util.h update.h MSFS.h
OBJ = PSXMSFS.o MSFS.o PSX.o connect.o util.o update.o main.o

all: PSXMSFS win exe
rel: all hash

comp: PSXMSFS win

PSXMSFS: $(OBJ) 
	$(CC) $(OBJ) -o PSXMSFS $(LINK)
	cp PSXMSFS.exe PSXMSFS

%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -D__MINGW__ -DVER=$(VER) -DCOMP="\"MINGW\"" -c $<

clean:
	rm -rf PSXMSFS *.o *.exe

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

win:
	cp *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
	cp *.h /home/stephan/NAS/TRANSFERT/pfpx/src
exe:
	cp PSXMSFS.exe /home/stephan/NAS/TRANSFERT/pfpx
