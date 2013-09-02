/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.validation.ui;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.Size;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.AbstractUIValidationContext;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ValidationUITest
{
   @Deployment
   @Dependencies({ @AddonDependency(name = "org.jboss.forge.addon:bean-validation"),
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:bean-validation"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));

      return archive;
   }

   @Inject
   @WithAttributes(label = "Name", required = true)
   @Size(min = 1, max = 5)
   private UIInput<String> name;

   @Test
   public void testValidation()
   {
      name.setValue("A very long string");
      final UIContext uiContext = new AbstractUIContext()
      {
         @Override
         public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection()
         {
            return Selections.emptySelection();
         }
      };
      AbstractUIValidationContext validationContext = new AbstractUIValidationContext()
      {
         @Override
         public UIContext getUIContext()
         {
            return uiContext;
         }
      };
      name.validate(validationContext);
      List<String> errors = validationContext.getErrors();
      Assert.assertFalse("An error should have been captured", errors.isEmpty());
   }
}
