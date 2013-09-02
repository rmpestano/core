/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.addon.validation.constraintvalidators;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * A {@link ConstraintValidator} for InputComponents
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class DelegatingValidator<T extends Annotation> implements
         ConstraintValidator<T, InputComponent<Object, Object>>
{

   private final ConstraintValidator<T, ?> singleValidator;
   private final ConstraintValidator<T, ?> manyValidator;

   public DelegatingValidator(ConstraintValidator<T, ?> singleValidator, ConstraintValidator<T, ?> manyValidator)
   {
      super();
      this.singleValidator = singleValidator;
      this.manyValidator = manyValidator;
   }

   @Override
   public void initialize(T constraintAnnotation)
   {
      singleValidator.initialize(constraintAnnotation);
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean isValid(InputComponent<Object, Object> value, ConstraintValidatorContext context)
   {
      Object valueFor = InputComponents.getValueFor(value);
      if (value instanceof ManyValued)
      {
         return ((ConstraintValidator<T, Object>) manyValidator).isValid(valueFor, context);
      }
      else
      {
         return ((ConstraintValidator<T, Object>) singleValidator).isValid(valueFor, context);
      }
   }
}
