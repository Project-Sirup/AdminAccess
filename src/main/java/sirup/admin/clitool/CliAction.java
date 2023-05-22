package sirup.admin.clitool;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CliAction {
    String command();
    String alias() default "";
    String description() default "";
}
