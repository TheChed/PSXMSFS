#include <stdint.h>



/*long unsigned 64 bit integer used to store time related functions
 *
 * Various functions used for time management
 * */
typedef uint64_t monotime ;

extern monotime TimeStart;

extern monotime getMonotonicTime(void);

  static inline void elapsedStart(monotime *start_time) {
       *start_time =getMonotonicTime();
  } 
  
  static inline uint64_t elapsedUs(monotime start_time) {
       return getMonotonicTime() - start_time;
   }
  
   static inline uint64_t elapsedMs(monotime start_time) {
       return elapsedUs(start_time) / 1000;
  }
