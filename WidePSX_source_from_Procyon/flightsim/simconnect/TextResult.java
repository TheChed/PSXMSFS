// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

import flightsim.simconnect.recv.RecvEvent;

public enum TextResult
{
    MENU_SELECT_1("MENU_SELECT_1", 0, 0), 
    MENU_SELECT_2("MENU_SELECT_2", 1, 1), 
    MENU_SELECT_3("MENU_SELECT_3", 2, 2), 
    MENU_SELECT_4("MENU_SELECT_4", 3, 3), 
    MENU_SELECT_5("MENU_SELECT_5", 4, 4), 
    MENU_SELECT_6("MENU_SELECT_6", 5, 5), 
    MENU_SELECT_7("MENU_SELECT_7", 6, 6), 
    MENU_SELECT_8("MENU_SELECT_8", 7, 7), 
    MENU_SELECT_9("MENU_SELECT_9", 8, 8), 
    MENU_SELECT_10("MENU_SELECT_10", 9, 9), 
    DISPLAYED("DISPLAYED", 10, 65536), 
    QUEUED("QUEUED", 11, 65537), 
    REMOVED("REMOVED", 12, 65538), 
    REPLACED("REPLACED", 13, 65539), 
    TIMEOUT("TIMEOUT", 14, 65540);
    
    private final int value;
    
    private TextResult(final String name, final int ordinal, final int value) {
        this.value = value;
    }
    
    public boolean isSelection() {
        return this.value < 256;
    }
    
    public int value() {
        return this.value;
    }
    
    public static TextResult type(final int val) {
        final TextResult[] values = values();
        TextResult[] array;
        for (int length = (array = values).length, i = 0; i < length; ++i) {
            final TextResult tr = array[i];
            if (tr.value == val) {
                return tr;
            }
        }
        return null;
    }
    
    public static TextResult type(final RecvEvent ev) {
        return type(ev.getData());
    }
}
