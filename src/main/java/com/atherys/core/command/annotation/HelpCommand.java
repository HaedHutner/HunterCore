package com.atherys.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HelpCommand {
    String title();

    /**
     * For use with sub-commands.
     */
    String prefix() default "";
}
