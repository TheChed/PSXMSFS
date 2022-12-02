g++ -g -c -IInclude PSX.cpp
g++ -g -c -IInclude connect.cpp
g++ -g -c -IInclude PSXMSFS.cpp
g++ -static PSXMSFS.o connect.o PSX.o -g -o PSXMSFS.exe -LInclude -lSimConnect -lwsock32 -lpthread -limagehlp
