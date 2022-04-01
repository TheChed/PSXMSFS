Bridge between PSX and MSFS
Very early stage but functionnal.

This started as a program to put in practice the C that I started learning a few months ago. Hence expect very ugly and non optimized code. But then, this is how you progress.


INSTALLATION:
you will need 3 dlls for windows which should be in the same directory as the executable
  * Simconnect.dll (which should be somewhere in your MSFS directoryi once you enable the SDK environment)
  * pthreadGC2.dll and pthreadVC2.dll that enable POSIX threads under Windows. Those can be found in any $
  good grocery, I use : https://sourceware.org/pub/pthreads-win32/dll-latest/dll/x64/

  For convenience sake, I have included those files in the Include directory.

  USAGE:
  Once all 4 files are in the same directory, just launch the program in a cmd window by typing: PSXMSFS.exe 192.168.x.xx 10747

  Replacing xx.xx.xx.xx with the IP address of the computer running PSX, and 10747 being the port for the main PSX server. The port of the boost server is expected to be 10749.

  
