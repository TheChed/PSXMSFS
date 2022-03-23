// 
// Decompiled by Procyon v0.5.36
// 

package flightsim.simconnect.wrappers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FlightSimData {
    String variable();
    
    String units() default "";
    
    int stringWidth() default 256;
}
