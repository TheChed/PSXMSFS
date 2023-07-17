#include <stdio.h>
#include "PSXMSFS.h"

int main(void)
{
    if (initialize(NULL,NULL)!=0) {
        printf("Could not initialize various parameters. Quitting now\n" );
        exit(EXIT_FAILURE);
    }

    if (connectPSXMSFS()!=0){
    
        printf("Could not connect PSX to MSFS. Quitting now\n" );
        exit(EXIT_FAILURE);
    }
    
    main_launch();
    cleanup();

    printf("Normal exit. See you soon...\n");
    return 0;
}
