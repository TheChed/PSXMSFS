// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class MutableBoolean
{
    private boolean value;
    
    public MutableBoolean() {
        this.value = false;
    }
    
    public MutableBoolean(final boolean argValue) {
        this.value = false;
        this.value = argValue;
    }
    
    public void set(final boolean argValue) {
        this.value = argValue;
    }
    
    public boolean get() {
        return this.value;
    }
}
