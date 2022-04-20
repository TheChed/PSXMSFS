gcc -g -Iinclude -c connect.cpp 
gcc -g -Iinclude -c PSX.cpp 
gcc -g -Iinclude -c PSXMSFS.cpp 
gcc -g -o PSXMSFS.exe PSXMSFS.o PSX.o connect.o -Lbin -lwsock32 -lSimConnect -lpthread 
