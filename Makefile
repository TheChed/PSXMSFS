CC = x86_64-w64-mingw32-gcc  
#CFLAGS = -g -IInclude -Wall -Wextra -pedantic -Werror
CFLAGS = -g -IInclude -Wall -Wextra -pedantic

DEPS = PSXMSFS.h
OBJ = PSXMSFS.o PSX.o connect.o util.o
OBJSIM = sim.o
OBJERR = err.o

all: PSXMSFS hash move win

simu: sim move win

error: err win

comp: PSXMSFS hash move

PSXMSFS: $(OBJ) $(DEPS)
	$(CC) -static $(OBJ) -g -o PSXMSFS.exe -LInclude -lSimConnect -lwsock32 -lpthread -limagehlp

sim: $(OBJSIM) 
	$(CC)  $(OBJSIM) -o sim.exe -LInclude -lSimConnect 

err: $(OBJERR) 
	$(CC)  $(OBJERR) -g -o err.exe -limagehlp

%.o : %.cpp
	$(CC) $(CFLAGS) -c $<

sim.o : sim.cpp
	$(CC) -IInclude -c $<

err.o : err.cpp
	$(CC) -g -IInclude -c $<

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
