/* File containing all UTIL functions such as coordinates calculations, time related functions
 * etc.
 */

#include <stdint.h>
#include <time.h>
#include "util.h"

monotime TimeStart;

monotime getMonotonicTime(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ((uint64_t)ts.tv_sec) * 1000000 + ts.tv_nsec / 1000;
}
