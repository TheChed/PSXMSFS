CC = x86_64-w64-mingw32-gcc  
CFLAGS = -IInclude -Wall -Wextra -Wpedantic -Werror

DEPS = PSXMSFS.h
OBJ = PSXMSFS.o PSX.o connect.o

all: PSXMSFS hash move win

comp: PSXMSFS hash move

PSXMSFS: $(OBJ) $(DEPS)
	$(CC) -static $(OBJ) -o PSXMSFS.exe -LInclude -lSimConnect -lwsock32 -lpthread

%.o : %.cpp
	$(CC) $(CFLAGS) -c $<

clean:
	rm -rf bin/PSXMSFS.* *.o *.exe

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

move:
	mv *.exe /home/stephan/Documents/C/src/PSX/bin

win:
	cp bin/*.exe /home/stephan/NAS/TRANSFERT/pfpx

src:
	cp *.h *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
