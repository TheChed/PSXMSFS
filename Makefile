CC = x86_64-w64-mingw32-g++
CFLAGS = -std=c++20 -IInclude -Werror -Wall -Wextra -pedantic
VER=$(shell date +"%y%m%d%H%M")
LINK= -LInclude -lwsock32 -lSimConnect

DEPS = PSXMSFSLIB.h util.h update.h MSFS.h log.h connect.h
OBJ = PSXDATA.o link.o PSXMSFS.o MSFS.o connect.o util.o update.o log.o
LIBOBJ = PSXDATA.o MSFS.o connect.o util.o update.o PSXMSFS.o log.o
OBJLINK = link.o

all: lib link win
rel: all hash

comp: lib win

lib: $(LIBOBJ)
	$(CC) $(LIBOBJ) -shared -fPIC -o PSXMSFS.dll $(LINK)
	cp PSXMSFS.dll Include/libPSXMSFS.a

link: $(OBJLINK) 
	$(CC) $(OBJLINK) -o LINK -lPSXMSFS $(LINK)
	cp LINK.exe LINK

%.o : %.cpp $(DEPS)
	$(CC) $(CFLAGS) -D__MINGW__ -DVER=$(VER) -DCOMP="\"MINGW\"" -c $<
	cp  $< /home/stephan/NAS/TRANSFERT/pfpx/src

tex:
	lualatex --shell-escape PSXMSFS.tex
clean:
	rm -rf LINK *.o *.exe *.dll *.aux *.log *.pdf

hash:
	md5sum PSXMSFS.exe > bin/PSXMSFS.MD5

win:
#	cp  *.cpp /home/stephan/NAS/TRANSFERT/pfpx/src
#	cp  *.h /home/stephan/NAS/TRANSFERT/pfpx/src
