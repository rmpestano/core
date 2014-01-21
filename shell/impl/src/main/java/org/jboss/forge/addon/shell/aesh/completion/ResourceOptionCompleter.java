/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.command.completer.CompleterInvocation;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.util.ResourceLister;
import org.jboss.forge.addon.shell.util.ResourceLister.Filter;
import org.jboss.forge.furnace.util.Assert;

/**
 * Completes {@link File} objects
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">St��le W. Pedersen</a>
 */
public class ResourceOptionCompleter implements OptionCompleter<CompleterInvocation>
{
   private final Filter filter;
   private final Resource<?> cwd;

   public ResourceOptionCompleter(Resource<?> cwd)
   {
      this(cwd, Filter.ALL);
   }

   public ResourceOptionCompleter(Resource<?> cwd, Filter filter)
   {
      Assert.notNull(cwd, "Current resource must not be null.");
      Assert.notNull(filter, "Filter type must be specified.");
      this.cwd = cwd;
      this.filter = filter;
   }

   @Override
   public void complete(CompleterInvocation completerData)
   {

      CompleteOperation completeOperation =
               new CompleteOperation(completerData.getAeshContext(), completerData.getGivenCompleteValue(), 0);
      if (completerData.getGivenCompleteValue() == null)
         new ResourceLister("", cwd, filter).findMatchingDirectories(completeOperation);
      else
         new ResourceLister(completerData.getGivenCompleteValue(), cwd, filter)
                  .findMatchingDirectories(completeOperation);

      if (completeOperation.getCompletionCandidates().size() > 1)
      {
         completeOperation.removeEscapedSpacesFromCompletionCandidates();
      }

      completerData.setCompleterValuesTerminalString(completeOperation.getCompletionCandidates());
      if (completerData.getGivenCompleteValue() != null && completerData.getCompleterValues().size() == 1)
      {
         completerData.setAppendSpace(completeOperation.hasAppendSeparator());
      }

      if (completeOperation.doIgnoreOffset())
         completerData.setIgnoreOffset(completeOperation.doIgnoreOffset());

      completerData.setIgnoreStartsWith(true);
   }

   public Filter getFilter()
   {
      return filter;
   }
}
