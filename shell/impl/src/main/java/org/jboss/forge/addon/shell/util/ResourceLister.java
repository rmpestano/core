/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;

/**
 * Helper class to list possible files during a complete operation.
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">St��le W. Pedersen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceLister
{
   private static final char DOT = '.';
   private static final String home = "~" + Config.getPathSeparator();
   private static final String parent = "..";
   private static final char STAR = '*';
   private static final char WILDCARD = '?';
   private static final char ESCAPE = '\\';

   private static final File[] windowsRoots = File.listRoots();

   private final String token;
   private final Resource<?> cwd;
   private String rest;
   private String lastDir;
   private ResourceFilter filter;
   private Comparator<String> fileComparator;

   public ResourceLister(String token, Resource<?> cwd)
   {
      if (token == null)
         throw new IllegalArgumentException("Incoming directory cannot be null");
      if (cwd == null)
         throw new IllegalArgumentException("Current working directory cannot be null");
      this.token = Parser.switchEscapedSpacesToSpacesInWord(token);
      this.cwd = cwd;
      findRestAndLastDir();
      setResourceFilter(Filter.ALL);
   }

   public ResourceLister(String token, Resource<?> cwd, Comparator<String> comparator)
   {
      this(token, cwd);
      this.fileComparator = comparator;
   }

   public ResourceLister(String token, Resource<?> cwd, Filter filter)
   {
      this(token, cwd);
      setResourceFilter(filter);
   }

   public ResourceLister(String token, Resource<?> cwd, Filter filter, Comparator<String> fileComparator)
   {
      this(token, cwd, filter);
      this.fileComparator = fileComparator;
   }

   public ResourceLister(String token, Resource<?> cwd, ResourceFilter filter)
   {
      this(token, cwd);
      this.filter = filter;
   }

   public ResourceLister(String token, Resource<?> cwd, ResourceFilter fileFilter, Comparator<String> fileComparator)
   {
      this(token, cwd, fileFilter);
      this.fileComparator = fileComparator;
   }

   private void setResourceFilter(Filter filter)
   {
      if (filter == Filter.ALL)
         this.filter = new FileOrDirectoryResourceFilter();
      else if (filter == Filter.FILE)
         this.filter = new OnlyFileResourceFilter();
      else if (filter == Filter.DIRECTORY)
         this.filter = new DirectoryResourceFilter();
      else
         this.filter = new NoDotNamesResourceFilter();
   }

   /**
    * findMatchingDirectories will try to populate the CompleteOperation object based on it initial params.
    * 
    * @param completion
    */
   public void findMatchingDirectories(CompleteOperation completion)
   {
      completion.doAppendSeparator(false);

      // if token is empty, just list cwd
      if (token.trim().isEmpty())
      {
         completion.addCompletionCandidates(listDirectory(cwd, null));
      }
      else if (containStar())
      {
      }
      else if (containWildCards())
      {
      }
      else if (startWithHome())
      {
         if (isHomeAndTokenADirectory())
         {
            if (tokenEndsWithSlash())
            {
               completion.addCompletionCandidates(
                        listDirectory(toResource(Config.getHomeDir() + token.substring(1)), null));
            }
            else
            {
               // completion.addCompletionCandidate(Config.getPathSeparator());
               List<String> tmpDirs = listDirectory(toResource(Config.getHomeDir()), token.substring(2));
               if (tmpDirs.size() == 1 || endsWithParent())
               {
                  completion.addCompletionCandidate(Config.getPathSeparator());
               }
               else
                  completion.addCompletionCandidates(tmpDirs);
            }
         }
         else if (isHomeAndTokenAFile())
         {
            completion.addCompletionCandidate("");
            // append when we have a file
            completion.doAppendSeparator(true);
         }
         // not a directory or file, list what we find
         else
         {
            listPossibleDirectories(completion);
         }
      }
      else if (!startWithSlash() && !startWithWindowsDrive())
      {
         if (isCwdAndTokenADirectory())
         {
            if (tokenEndsWithSlash())
            {
               completion.addCompletionCandidates(listDirectory(
                        toResource(cwd.getFullyQualifiedName() + Config.getPathSeparator() + token), null));
            }
            else
            {
               List<String> tmpDirs = listDirectory(cwd, token);
               if (tmpDirs.size() == 1 || endsWithParent())
                  completion.addCompletionCandidate(Config.getPathSeparator());
               else
                  completion.addCompletionCandidates(tmpDirs);
            }
         }
         else if (isCwdAndTokenAFile())
         {
            listPossibleDirectories(completion);
            if (completion.getCompletionCandidates().size() == 1)
            {
               completion.getCompletionCandidates().set(0, new TerminalString("", true));
               // append when we have a file
               completion.doAppendSeparator(true);
            }
            else if (completion.getCompletionCandidates().size() == 0)
            {
               completion.addCompletionCandidate("");
               // append when we have a file
               completion.doAppendSeparator(true);
            }
         }
         // not a directory or file, list what we find
         else
         {
            listPossibleDirectories(completion);
         }
      }
      else if (startWithSlash() || startWithWindowsDrive())
      {
         if (isTokenADirectory())
         {
            if (tokenEndsWithSlash())
            {
               completion.addCompletionCandidates(
                        listDirectory(toResource(token), null));
            }
            else
               completion.addCompletionCandidate(Config.getPathSeparator());
         }
         else if (isTokenAFile())
         {
            listPossibleDirectories(completion);
            if (completion.getCompletionCandidates().size() == 1)
            {
               completion.getCompletionCandidates().set(0, new TerminalString("", true));
               // completion.addCompletionCandidate("");
               // append when we have a file
               completion.doAppendSeparator(true);
            }
         }
         // not a directory or file, list what we find
         else
         {
            listPossibleDirectories(completion);
         }
      }

      // try to find if more than one filename start with the same word
      if (completion.getCompletionCandidates().size() > 1)
      {
         String startsWith = Parser.findStartsWithTerminalString(completion.getCompletionCandidates());
         if (startsWith.contains(" "))
            startsWith = Parser.switchEscapedSpacesToSpacesInWord(startsWith);
         if (startsWith != null && startsWith.length() > 0 && rest != null &&
                  startsWith.length() > rest.length())
         {
            completion.getCompletionCandidates().clear();
            completion.addCompletionCandidate(Parser.switchSpacesToEscapedSpacesInWord(startsWith));
         }
      }

      // new offset tweaking to match the "common" way of returning completions
      if (completion.getCompletionCandidates().size() == 1)
      {
         if (isTokenADirectory() && !tokenEndsWithSlash())
         {
            completion.getCompletionCandidates().get(0).setCharacters(token +
                     completion.getCompletionCandidates().get(0).getCharacters());

            completion.setOffset(completion.getCursor() - token.length());
         }
         else if (isTokenAFile())
         {
            completion.getCompletionCandidates().get(0).setCharacters(token +
                     completion.getCompletionCandidates().get(0).getCharacters());

            completion.setOffset(completion.getCursor() - token.length());
            completion.doAppendSeparator(true);
         }
         else if (token != null)
         {
            if (rest != null && token.length() > rest.length())
            {
               completion.getCompletionCandidates().get(0).setCharacters(
                        Parser.switchSpacesToEscapedSpacesInWord(
                                 token.substring(0, token.length() - rest.length())) +
                                 completion.getCompletionCandidates().get(0).getCharacters());

               completion.setOffset(completion.getCursor() - token.length());
            }
            else if (rest != null && token.length() == rest.length())
            {
               completion.setOffset(completion.getCursor() - (rest.length() + Parser.findNumberOfSpacesInWord(rest)));
            }
            else
            {
               if (token.endsWith(Config.getPathSeparator()))
                  completion.getCompletionCandidates().get(0).setCharacters(
                           Parser.switchSpacesToEscapedSpacesInWord(token) +
                                    completion.getCompletionCandidates().get(0).getCharacters());

               completion.setOffset(completion.getCursor() - token.length());
            }
         }
         else
         {
            completion.setOffset(completion.getCursor());
         }
      }
      else if (completion.getCompletionCandidates().size() > 1)
      {
         completion.setIgnoreOffset(true);
         if (rest != null && rest.length() > 0)
            completion.setOffset(completion.getCursor() - rest.length());
      }
   }

   private void listPossibleDirectories(CompleteOperation completion)
   {
      List<String> returnResources;

      if (startWithSlash())
      {
         if (lastDir != null && lastDir.startsWith(Config.getPathSeparator()))
            returnResources = listDirectory(toResource(lastDir), rest);
         else
            returnResources = listDirectory(toResource(Config.getPathSeparator() + lastDir), rest);
      }
      else if (startWithWindowsDrive())
      {
         if (lastDir != null && lastDir.length() == 2)
            returnResources = listDirectory(toResource(lastDir + Config.getPathSeparator()), rest);
         else
            returnResources = listDirectory(toResource(lastDir), rest);
      }
      else if (startWithHome())
      {
         if (lastDir != null)
         {
            returnResources = listDirectory(toResource(Config.getHomeDir() + lastDir.substring(1)), rest);
         }
         else
            returnResources = listDirectory(toResource(Config.getHomeDir() + Config.getPathSeparator()), rest);
      }
      else if (lastDir != null)
      {
         returnResources = listDirectory(toResource(cwd +
                  Config.getPathSeparator() + lastDir), rest);
      }
      else
         returnResources = listDirectory(cwd, rest);

      completion.addCompletionCandidates(returnResources);
   }

   private void findRestAndLastDir()
   {
      if (token.contains(Config.getPathSeparator()))
      {
         lastDir = token.substring(0, token.lastIndexOf(Config.getPathSeparator()));
         rest = token.substring(token.lastIndexOf(Config.getPathSeparator()) + 1);
      }
      else
      {
         if (toResource(cwd + Config.getPathSeparator() + token).exists())
            lastDir = token;
         else
            rest = token;
      }
   }

   private boolean isTokenADirectory()
   {
      return toResource(token) instanceof DirectoryResource;
   }

   private boolean isTokenAFile()
   {
      Resource<?> resource = toResource(token);
      return (resource instanceof FileResource) && !(resource instanceof DirectoryResource) && resource.exists();
   }

   private boolean isCwdAndTokenADirectory()
   {
      return toResource(cwd.getFullyQualifiedName() + Config.getPathSeparator() + token) instanceof DirectoryResource;
   }

   private boolean isCwdAndTokenAFile()
   {
      Resource<?> resource = toResource(cwd.getFullyQualifiedName() + Config.getPathSeparator() + token);
      return (resource instanceof FileResource) && !(resource instanceof DirectoryResource) && resource.exists();
   }

   private boolean isHomeAndTokenADirectory()
   {
      return toResource(Config.getHomeDir() + token.substring(1)) instanceof DirectoryResource;
   }

   private boolean isHomeAndTokenAFile()
   {
      Resource<?> resource = toResource(Config.getHomeDir() + token.substring(1));
      return (resource instanceof FileResource) && !(resource instanceof DirectoryResource) && resource.exists();
   }

   private boolean endsWithParent()
   {
      return token.lastIndexOf(parent) == token.length() - 2;
   }

   private boolean startWithHome()
   {
      return token.indexOf(home) == 0;
   }

   private boolean containStar()
   {
      int index = token.indexOf(STAR);
      if (index == 0 || (index > 0 && !(token.charAt(index - 1) == ESCAPE)))
         return true;
      else
         return false;
   }

   private boolean containWildCards()
   {
      int index = token.indexOf(WILDCARD);
      if (index == 0 || (index > 0 && !(token.charAt(index - 1) == ESCAPE)))
         return true;
      else
         return false;
   }

   private boolean startWithSlash()
   {
      return token.indexOf(Config.getPathSeparator()) == 0;
   }

   private boolean startWithWindowsDrive()
   {
      if (!Config.isOSPOSIXCompatible())
      {
         for (Resource<?> f : toResources(windowsRoots))
         {
            if (token.startsWith(f.toString()))
            {
               return true;
            }
         }
         return false;
      }
      else
         return false;
   }

   private Resource<?> toResource(String path)
   {
      return cwd.getResourceFactory().create(new File(path));
   }

   private List<Resource<?>> toResources(File[] files)
   {
      List<Resource<?>> result = new ArrayList<>();
      for (File file : files)
      {
         result.add(cwd.getResourceFactory().create(file));
      }
      return result;
   }

   private boolean tokenEndsWithSlash()
   {
      return token.lastIndexOf(Config.getPathSeparator()) == token.length() - 1;
   }

   private List<String> listDirectory(Resource<?> root, String rest)
   {
      List<String> fileNames = new ArrayList<>();
      if (root != null)
      {
         for (Resource<?> resource : root.listResources(filter))
         {
            if (rest == null || rest.length() == 0)
               if (resource instanceof DirectoryResource)
                  fileNames.add(Parser.switchSpacesToEscapedSpacesInWord(resource.getName())
                           + Config.getPathSeparator());
               else
                  fileNames.add(Parser.switchSpacesToEscapedSpacesInWord(resource.getName()));
            else
            {
               if (resource.getName().startsWith(rest))
               {
                  if (resource instanceof DirectoryResource)
                     fileNames.add(Parser.switchSpacesToEscapedSpacesInWord(resource.getName())
                              + Config.getPathSeparator());
                  else
                     fileNames.add(Parser.switchSpacesToEscapedSpacesInWord(resource.getName()));
               }
            }
         }
      }

      if (fileComparator == null)
         fileComparator = new PosixFileNameComparator();
      Collections.sort(fileNames, fileComparator);
      return fileNames;
   }

   @Override
   public String toString()
   {
      return "ResourceLister{" +
               "token='" + token + '\'' +
               ", cwd=" + cwd +
               ", rest='" + rest + '\'' +
               ", lastDir='" + lastDir + '\'' +
               '}';
   }

   public static class DirectoryResourceFilter implements ResourceFilter
   {
      @Override
      public boolean accept(Resource<?> resource)
      {
         return (resource instanceof DirectoryResource) && resource.exists();
      }
   }

   public static class FileOrDirectoryResourceFilter implements ResourceFilter
   {
      @Override
      public boolean accept(Resource<?> resource)
      {
         return (resource instanceof FileResource || resource instanceof DirectoryResource) && resource.exists();
      }
   }

   public static class OnlyFileResourceFilter implements ResourceFilter
   {
      @Override
      public boolean accept(Resource<?> resource)
      {
         return !(resource instanceof DirectoryResource);
      }
   }

   public static class NoDotNamesResourceFilter implements ResourceFilter
   {
      @Override
      public boolean accept(Resource<?> resource)
      {
         return !resource.getName().startsWith(".");
      }
   }

   public enum Filter
   {
      FILE, DIRECTORY, ALL, NO_DOT_NAMES
   }

   public static class PosixFileNameComparator implements Comparator<String>
   {
      @Override
      public int compare(String o1, String o2)
      {
         if (o1.length() > 1 && o2.length() > 1)
         {
            if (o1.indexOf(DOT) == 0)
            {
               if (o2.indexOf(DOT) == 0)
                  return o1.substring(1).compareToIgnoreCase(o2.substring(1));
               else
                  return o1.substring(1).compareToIgnoreCase(o2);
            }
            else
            {
               if (o2.indexOf(DOT) == 0)
                  return o1.compareToIgnoreCase(o2.substring(1));
               else
                  return o1.compareToIgnoreCase(o2);
            }
         }
         else
            return 0;
      }
   }
}
