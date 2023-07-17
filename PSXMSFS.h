#ifndef __PSXMSFS_H_
#define __PSXMSFS_H_
#include <windows.h>

typedef struct server_options server_options;
typedef struct flags flags_options;
typedef struct FLAGS PSXMSFSFLAGS;

extern "C" __declspec(dllimport) DWORD initialize(server_options *server, flags *flags);
extern "C" __declspec(dllimport) DWORD connectPSXMSFS(void);
extern "C" __declspec(dllimport) DWORD main_launch(void);
extern "C" __declspec(dllimport) DWORD cleanup(void);
#endif
