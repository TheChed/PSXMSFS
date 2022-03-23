// 
// Decompiled by Procyon v0.5.36
// 

package util;

public interface ObservableData
{
    void addObserver(final ObserverData p0);
    
    void removeObserver(final ObserverData p0);
    
    void notifyObservers(final ObservableData p0, final Object p1, final Object p2);
}
