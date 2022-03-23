// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class MutableString
{
    private String value;
    
    public MutableString() {
        this.value = new String("");
    }
    
    public MutableString(final String argValue) {
        this.value = new String("");
        this.value = argValue;
    }
    
    public void set(final String argValue) {
        this.value = argValue;
    }
    
    public String get() {
        return this.value;
    }
}
