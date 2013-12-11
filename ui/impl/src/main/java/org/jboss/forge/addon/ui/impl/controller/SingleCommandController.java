/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.UIProgressMonitor;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.controller.CommandExecutionListener;
import org.jboss.forge.addon.ui.impl.context.UIBuilderImpl;
import org.jboss.forge.addon.ui.impl.context.UIExecutionContextImpl;
import org.jboss.forge.addon.ui.impl.context.UIValidationContextImpl;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.validation.UIValidationMessage;
import org.jboss.forge.addon.ui.validation.UIValidationMessage.Severity;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class SingleCommandController extends AbstractCommandController
{
   private UIBuilderImpl uiBuilder;

   public SingleCommandController(AddonRegistry addonRegistry, UIRuntime runtime, UICommand command)
            throws Exception
   {
      super(addonRegistry, runtime, command);
   }

   @Override
   public void initialize() throws Exception
   {
      if (!isInitialized())
      {
         uiBuilder = new UIBuilderImpl(context);
         initialCommand.initializeUI(uiBuilder);
      }
   }

   @Override
   public boolean isInitialized()
   {
      return (uiBuilder != null);
   }

   protected void assertInitialized()
   {
      Assert.isTrue(isInitialized(), "Controller must be initialized.");
   }

   @Override
   public Result execute() throws Exception
   {
      assertInitialized();
      UIProgressMonitor progressMonitor = runtime.createProgressMonitor(context);
      UIExecutionContextImpl executionContext = new UIExecutionContextImpl(context, progressMonitor);

      Imported<CommandExecutionListener> listeners = addonRegistry.getServices(CommandExecutionListener.class);
      try
      {
         if (!isValid())
         {
            throw new IllegalStateException("Command is not in valid state and cannot be executed.");
         }

         for (CommandExecutionListener listener : listeners)
         {
            listener.preCommandExecuted(initialCommand, executionContext);
         }

         try
         {
            Result result = initialCommand.execute(executionContext);
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandExecuted(initialCommand, executionContext, result);
            }
            return result;
         }
         catch (Exception e)
         {
            for (CommandExecutionListener listener : listeners)
            {
               listener.postCommandFailure(initialCommand, executionContext, e);
            }
            throw e;
         }
      }
      finally
      {
         for (CommandExecutionListener listener : listeners)
         {
            listeners.release(listener);
         }
         context.close();
      }
   }

   @Override
   public List<InputComponent<?, ?>> getInputs()
   {
      assertInitialized();
      return new ArrayList<InputComponent<?, ?>>(uiBuilder.getInputs().values());
   }

   @Override
   public CommandController setValueFor(String inputName, Object value)
   {
      assertInitialized();
      InputComponent<?, Object> input = getInput(inputName);
      InputComponents.setValueFor(getConverterFactory(), input, value);
      return this;
   }

   @Override
   public Object getValueFor(String inputName) throws IllegalArgumentException
   {
      assertInitialized();
      InputComponent<?, Object> input = getInput(inputName);
      return InputComponents.getValueFor(input);
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return initialCommand.getMetadata(context);
   }

   @Override
   public boolean isEnabled()
   {
      return initialCommand.isEnabled(context);
   }

   @Override
   public InputComponent<?, Object> getInput(String inputName)
   {
      assertInitialized();
      Map<String, InputComponent<?, Object>> inputs = uiBuilder.getInputs();
      InputComponent<?, Object> inputComponent = inputs.get(inputName);
      if (inputComponent == null)
      {
         throw new IllegalArgumentException("No such input [" + inputName + "].");
      }
      return inputComponent;
   }

   @Override
   public List<UIValidationMessage> validate()
   {
      assertInitialized();
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      for (InputComponent<?, ?> inputComponent : getInputs())
      {
         validationContext.setCurrentInputComponent(inputComponent);
         inputComponent.validate(validationContext);
      }
      validationContext.setCurrentInputComponent(null);
      initialCommand.validate(validationContext);
      return validationContext.getMessages();
   }

   @Override
   public boolean hasInput(String inputName)
   {
      assertInitialized();
      Map<String, InputComponent<?, Object>> inputs = uiBuilder.getInputs();
      return inputs.containsKey(inputName);
   }

   @Override
   public boolean isValid()
   {
      for (UIValidationMessage message : validate())
      {
         if (message.getSeverity() == Severity.ERROR)
            return false;
      }
      return true;
   }

   @Override
   public void close() throws Exception
   {
      context.close();
   }

   @Override
   public UICommand getCommand()
   {
      return initialCommand;
   }

   protected ConverterFactory getConverterFactory()
   {
      return addonRegistry.getServices(ConverterFactory.class).get();
   }
}
