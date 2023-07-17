#include <windows.h>

extern "C" __declspec(dllimport) DWORD cleanup(void);
extern "C" __declspec(dllimport) DWORD main_launch(void);
extern "C" __declspec(dllimport) DWORD initialize(int argc, char **argv);
