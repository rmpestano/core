package br.ufrgs.rmpestano.intrabundle.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by rmpestano on 1/26/14.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER, TYPE, FIELD })
public @interface CurrentLocale {
}
