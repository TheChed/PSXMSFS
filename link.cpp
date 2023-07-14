#include <stdio.h>
#include "PSXMSFS.h"

int main(void)
{
    FLAGS *ini;
    ini = initialize(NULL, NULL);
    if (ini == NULL) {
        printf("Something went wrong, I cannot proceed. Quitting now\n");
        exit(EXIT_FAILURE);
    }

    main_launch(ini);
    cleanup(ini);

    printf("Normal exit. See you soon...\n");
    return 0;
}
