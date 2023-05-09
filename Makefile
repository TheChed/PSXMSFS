CC = x86_64-w64-mingw32-gcc  
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic

DEPS = PSXMSFS.h util.h update.h MSFS.h
OBJ = PSXMSFS.o MSFS.o PSX.o connect.o util.o update.o

all: PSXMSFS move win
rel: all hash

comp: PSXMSFS

PSXMSFS: $(OBJ) 
	$(CC) -static $(OBJ) -o PSXMSFS.exe -LInclude -lSimConnect -lwsock32 -lpthread 

.PHONY: PSXMSFS
%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -c $<

clean:
	rm -rf bin/PSXMSFS.* *.o *.exe

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

move:
	mv *.exe /home/stephan/Documents/C/src/PSX/bin

win:
	cp *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
	cp *.h /home/stephan/NAS/TRANSFERT/pfpx/src
	cp bin/*.exe /home/stephan/NAS/TRANSFERT/pfpx

src:
	cp *.h *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
