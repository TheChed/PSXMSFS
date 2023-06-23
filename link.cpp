#include <stdio.h>
#include "PSXMSFS.h"

int main(int argc, char **argv)
{

    initialize(argc, argv);

    main_launch();

    cleanup();

    printf("Normal exit. See you soon...\n");
    return 0;
}
