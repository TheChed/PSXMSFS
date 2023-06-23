#include <stdio.h>
#include <windows.h>
#include "PSXMSFS.h"
#include "util.h"
#include "MSFS.h"
#include "update.h"


int main(int argc, char **argv)
{

   
    initialize(argc, argv);
    /*
     * Initialise and connect to all sockets: PSX, PSX Boost and Simconnect
     */
    if (!open_connections()) {
        exit(EXIT_FAILURE);
    }

    // initialize the data to be received as well as all EVENTS
    init_MS_data();

    /*
     * Sending Q423 DEMAND variable to PSX for the winds
     * Sending Q480 DEMAND variable to get aileron, rudder and elevator position
     */

    sendQPSX("demand=Qs483");
    sendQPSX("demand=Qs480");
    sendQPSX("demand=Qs562");

    /*
     * Initializing position of the plane
     * as boost and main threads are not yet available
     */

    init_pos();

    main_launch();

    cleanup();

    printf("Normal exit. See you soon...\n");
    return 0;
}
