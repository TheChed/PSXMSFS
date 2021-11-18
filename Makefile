#-static  main.cpp -L. -lSimConnect  -mwindows
CC = x86_64-w64-mingw32-gcc  
CFLAGS = -IInclude -Wall -Wextra -pedantic

DEPS = PSXMSFS.h
OBJ = PSXMSFS.o PSX.o connect.o

all: PSXMSFS move


testMSFS: testMSFS.o
		$(CC) -static testMSFS.o -o testMSFS.exe -LInclude -lSimConnect -lwsock32

PSXMSFS: $(OBJ) $(DEPS)
	$(CC) -static $(OBJ) -o PSXMSFS.exe -LInclude -lSimConnect -lwsock32

%.o : %.cpp
	$(CC) $(CFLAGS) -c $<

testMSFS.o: testMSFS.cpp
	$(CC) $(CFLAGS) -c $<

clean:
	rm -rf PSXMSFS.exe *.o *.exe

move:
	cp *.exe /home/stephan/NAS/TRANSFERT/pfpx

