/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.completion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.aesh.cl.completer.CompleterData;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.parser.Parser;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.util.InputComponents;

/**
 * Called when auto-completion of a {@link UISelectOne} or {@link UISelectMany} component is needed
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class SelectComponentOptionCompleter implements OptionCompleter
{
   private final SelectComponent<?, Object> selectComponent;
   private final ConverterFactory converterFactory;

   public SelectComponentOptionCompleter(SelectComponent<?, Object> selectComponent,
            ConverterFactory converterFactory)
   {
      super();
      this.selectComponent = selectComponent;
      this.converterFactory = converterFactory;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void complete(final CompleterData completerData)
   {
      final String completeValue = completerData.getGivenCompleteValue();
      Converter<Object, String> itemLabelConverter = (Converter<Object, String>) InputComponents
               .getItemLabelConverter(converterFactory, selectComponent);
      Iterable<Object> valueChoices = selectComponent.getValueChoices();
      List<String> choices = new ArrayList<String>();
      for (Object choice : valueChoices)
      {
         String convert = itemLabelConverter.convert(choice);
         if (convert != null)
         {
            choices.add(convert);
         }
      }
      // Remove already set values in many valued components
      if (selectComponent instanceof ManyValued)
      {
         Object value = InputComponents.getValueFor(selectComponent);
         if (value != null)
         {
            if (value instanceof Iterable)
            {
               Iterator<Object> it = ((Iterable<Object>) value).iterator();
               while (it.hasNext())
               {
                  Object next = it.next();
                  String convert = itemLabelConverter.convert(next);
                  choices.remove(convert);
               }
            }
            else
            {
               String convert = itemLabelConverter.convert(value);
               choices.remove(convert);
            }
         }
      }
      if (choices.size() > 1)
      {
         String startsWith = Parser.findStartsWith(choices);
         if (startsWith.length() > completeValue.length())
         {
            String substring = startsWith.substring(completeValue.length());
            completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(substring));
            completerData.setAppendSpace(false);
         }
         else
         {
            for (String choice : choices)
            {
               if (completeValue.isEmpty() || choice.startsWith(completeValue))
               {
                  completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(choice));
               }
            }
         }
      }
      else if (choices.size() == 1)
      {
         String candidate = choices.get(0).substring(completeValue.length());
         completerData.addCompleterValue(Parser.switchSpacesToEscapedSpacesInWord(candidate));
      }
   }
}