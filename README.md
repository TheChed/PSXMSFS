Bridge between PSX and MSFS
Very early stage but functionnal.

This started as a program to put in practice the C that I started learning a few months ago. Hence expect very ugly and non optimized code. But then, this is how you progress.

This tool is intended to work with MSFS on Windows10 (other versions not tested). 
The SDK used is very similar with the one of P3D and this might work with P3D, but I have not tested it.

One main advantage of this tool, is that is does not require any configuration
and should work out of the box.


#
# CHANGES
#


20/04/2022:
No more need for the pthread*.dll, which solves a few stability bugs.

10/04/2022:
Added COMM + XPDR synch

09/04/2022:
Functional TCAS

03/04/2022:
Added Nose gear steering sync
Added parsing command line
Added server+ports selections

02/04/2022:
Added time synchronization with PSX
Added rudder+elevator+aileron sync. Seems that there is a bug in MSFS: when ailerons move, spoilers move too.
Added PArking brakes sync


#
# INSTALLATION:
#


You will need the API dll from MS FlightSimulator:
  * Simconnect.dll (which should be somewhere in your MSFS directory once you enable the SDK environment)

For convenience sake, I have included this file in the bin directory.


 
#
# USAGE:
#


Once the DLL and the exe are in the same directory, just launch the program in a cmd window by typing: PSXMSFS.exe. Please make sure you launch the file via a command line and not by clicking on it in the Windown Manager.
  
 PSXMSFS.exe -h gives a brief help

Replacing xx.xx.xx.xx with the IP address of the computer running PSX, and 10747 being the port for the main PSX server. The port of the boost server is expected to be 10749.

 

#
# BUILDING FROM SOURCE:
#


All sources are on the github link: https://github.com/TheChed/PSXMSFS

The executable has been compiled under Linux under a Mingw64 environnent so to be able to create Windows compatible executables. A minimal Makefile is provided for the compile options I used.

The same Mingw64 environment for windws can be downloaded at: 
  https://www.mingw-w64.org/

Please make sure to include the pthread option when installing Mingw64 in your environement.

More info about the project:
  https://en.wikipedia.org/wiki/MinGW

You will also need the following files, included in the Include directory to link the executable.
    * Simconnect.h
    * SimConnect.lib


Last warning: this tool is still in a very early beta version and probably very buggy. Please feel free to report or propose any enhancement to improve its quality. 
