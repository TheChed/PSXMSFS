CC = x86_64-w64-mingw32-gcc  
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic
VER=$(shell date +"%y%m%d%H%M")

DEPS = PSXMSFS.h util.h update.h MSFS.h
OBJ = PSXMSFS.o MSFS.o PSX.o connect.o util.o update.o

all: PSXMSFS win
rel: all hash

comp: PSXMSFS

PSXMSFS: $(OBJ) 
	$(CC) -static $(OBJ) -o PSXMSFS_SPT -LInclude -lSimConnect -lwsock32 -lpthread 
	$(CC) -static $(OBJ) -o PSXMSFS_SWT -LInclude -lSimConnect -lwsock32 -lwinpthread 
	$(CC) $(OBJ) -o PSXMSFS_DPT -LInclude -lSimConnect -lwsock32 -lpthread 
	$(CC) $(OBJ) -o PSXMSFS_DWT -LInclude -lSimConnect -lwsock32 -lwinpthread 
	cp PSXMSFS.exe PSXMSFS

%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -c $<

clean:
	rm -rf PSXMSFS *.o *.exe

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

win:
	cp *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
	cp *.h /home/stephan/NAS/TRANSFERT/pfpx/src
	cp PSXMSFS.exe /home/stephan/NAS/TRANSFERT/pfpx
