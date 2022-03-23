// 
// Decompiled by Procyon v0.5.36
// 

package util;

import java.util.Random;

public class Randomizer
{
    private static Random randomGenerator;
    
    static {
        Randomizer.randomGenerator = new Random();
    }
    
    public static int getRand(final int argMax) {
        return Randomizer.randomGenerator.nextInt(argMax + 1);
    }
    
    public static int getBoundRand(final int argMin, final int argMax) {
        final int n = argMax - argMin + 1;
        final int i = Randomizer.randomGenerator.nextInt(n);
        return argMin + i;
    }
}
