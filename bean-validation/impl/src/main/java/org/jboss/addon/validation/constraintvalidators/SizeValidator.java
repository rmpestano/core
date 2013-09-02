/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.addon.validation.constraintvalidators;

import javax.validation.constraints.Size;

import org.hibernate.validator.internal.constraintvalidators.SizeValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.SizeValidatorForCollection;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * Size Validator for {@link InputComponents}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class SizeValidator extends DelegatingValidator<Size>
{
   public SizeValidator()
   {
      super(new SizeValidatorForCharSequence(), new SizeValidatorForCollection());
   }
}
