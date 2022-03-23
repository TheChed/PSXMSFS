// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class MutableDouble
{
    private double value;
    
    public MutableDouble() {
        this.value = 0.0;
    }
    
    public MutableDouble(final double argValue) {
        this.value = 0.0;
        this.value = argValue;
    }
    
    public void set(final double argValue) {
        this.value = argValue;
    }
    
    public double get() {
        return this.value;
    }
}
