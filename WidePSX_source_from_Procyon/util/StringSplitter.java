// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class StringSplitter
{
    private final String value;
    
    public StringSplitter(final String argValue) {
        this.value = new String(argValue);
    }
    
    public String splitFrom(final char splitFrom, final int argNth, final boolean argIncluded) {
        String temp = new String(this.value);
        int pos = 0;
        int from = 0;
        for (int i = 0; i < argNth; ++i) {
            pos = this.value.indexOf(splitFrom, from);
            if (pos == -1) {
                temp = this.value;
                break;
            }
            from = pos + 1;
        }
        if (pos != -1) {
            if (argIncluded) {
                temp = this.value.substring(pos);
            }
            else {
                temp = this.value.substring(pos + 1);
            }
        }
        return temp;
    }
    
    public String splitTo(final char splitTo, final int argNth, final boolean argIncluded) {
        String temp = new String(this.value);
        int pos = 0;
        int from = 0;
        for (int i = 0; i < argNth; ++i) {
            pos = this.value.indexOf(splitTo, from);
            if (pos == -1) {
                temp = this.value;
                break;
            }
            from = pos + 1;
        }
        if (pos != -1) {
            if (argIncluded) {
                temp = this.value.substring(0, pos + 1);
            }
            else {
                temp = this.value.substring(0, pos);
            }
        }
        return temp;
    }
    
    public String splitBetween(final char argFrom, final char argTo, final int argFirstOccurence, final int argSecOccurence, final boolean argFromIncluded, final boolean toIncluded) {
        String temp = new String(this.value);
        int pos = 0;
        int from = 0;
        for (int i = 0; i < argSecOccurence; ++i) {
            pos = temp.indexOf(argTo, from);
            if (pos == -1) {
                break;
            }
            from = pos + 1;
        }
        if (pos != -1) {
            if (toIncluded) {
                temp = temp.substring(0, pos + 1);
            }
            else {
                temp = temp.substring(0, pos);
            }
        }
        pos = 0;
        from = 0;
        for (int i = 0; i < argFirstOccurence; ++i) {
            pos = temp.indexOf(argFrom, from);
            if (pos == -1) {
                break;
            }
            from = pos + 1;
        }
        if (pos != -1) {
            if (argFromIncluded) {
                temp = temp.substring(pos);
            }
            else {
                temp = temp.substring(pos + 1);
            }
        }
        return temp;
    }
}
