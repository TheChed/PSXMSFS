// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect;

public enum TextType
{
    SCROLL_BLACK("SCROLL_BLACK", 0, 0), 
    SCROLL_WHITE("SCROLL_WHITE", 1, 1), 
    SCROLL_RED("SCROLL_RED", 2, 2), 
    SCROLL_GREEN("SCROLL_GREEN", 3, 3), 
    SCROLL_BLUE("SCROLL_BLUE", 4, 4), 
    SCROLL_YELLOW("SCROLL_YELLOW", 5, 5), 
    SCROLL_MAGENTA("SCROLL_MAGENTA", 6, 6), 
    SCROLL_CYAN("SCROLL_CYAN", 7, 7), 
    PRINT_BLACK("PRINT_BLACK", 8, 256), 
    PRINT_WHITE("PRINT_WHITE", 9, 257), 
    PRINT_RED("PRINT_RED", 10, 258), 
    PRINT_GREEN("PRINT_GREEN", 11, 259), 
    PRINT_BLUE("PRINT_BLUE", 12, 260), 
    PRINT_YELLOW("PRINT_YELLOW", 13, 261), 
    PRINT_MAGENTA("PRINT_MAGENTA", 14, 262), 
    PRINT_CYAN("PRINT_CYAN", 15, 263), 
    MENU("MENU", 16, 512);
    
    private final int value;
    
    private TextType(final String name, final int ordinal, final int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
}
