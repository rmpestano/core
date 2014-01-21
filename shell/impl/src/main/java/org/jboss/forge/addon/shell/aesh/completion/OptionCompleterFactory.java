package org.jboss.forge.addon.shell.aesh.completion;

import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.console.command.completer.CompleterInvocation;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.ResourceLister.Filter;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;

/**
 * Returns the completion based on the input component
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
public class OptionCompleterFactory
{
   public static OptionCompleter<CompleterInvocation> getCompletionFor(InputComponent<?, ?> component,
            ShellContext context, ConverterFactory converterFactory)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();

      String inputType = component.getFacet(HintsFacet.class).getInputType();
      OptionCompleter<CompleterInvocation> strategy = null;
      if (InputType.FILE_PICKER.equals(inputType))
      {
         strategy = new ResourceOptionCompleter(selection.get(), Filter.ALL);
      }
      else if (InputType.DIRECTORY_PICKER.equals(inputType))
      {
         strategy = new ResourceOptionCompleter(selection.get(), Filter.DIRECTORY);
      }
      else if (component instanceof SelectComponent)
      {
         strategy = new SelectComponentOptionCompleter((SelectComponent<?, Object>) component, converterFactory);
      }
      else if (Resource.class.isAssignableFrom(component.getValueType()))
      {
         // fall back to Resource completion.
         strategy = new ResourceOptionCompleter(selection.get(), Filter.ALL);
      }
      // Always try UICompleter first and then fallback to the chosen strategy
      strategy = new UICompleterOptionCompleter(strategy, context, component, converterFactory);
      return strategy;
   }
}
