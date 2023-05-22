package sirup.admin.clitool;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CliArgs {
    CliArg[] value();


     @Retention(RetentionPolicy.RUNTIME)
     @Target(ElementType.METHOD)
     @Repeatable(CliArgs.class)
     @interface CliArg {
         String flag();
         String arg() default "";
         String description() default "";
     }
}

