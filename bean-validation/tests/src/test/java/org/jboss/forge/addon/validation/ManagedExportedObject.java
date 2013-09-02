/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.validation;

import javax.validation.constraints.NotNull;

import org.jboss.forge.furnace.services.Exported;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Exported
public class ManagedExportedObject
{
   public String sayHello(@NotNull String name)
   {
      return "Hello," + name;
   }
}
