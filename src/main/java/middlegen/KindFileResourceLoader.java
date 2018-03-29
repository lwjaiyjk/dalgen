/*
 * Copyright (c) 2001, Aslak Hellesøy, BEKK Consulting
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of BEKK Consulting nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package middlegen;

import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.JarResourceLoader;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.commons.collections.ExtendedProperties;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * A loader that returns an empty InputStream (in stead of throwing an
 * exception) if a file isn't found.
 *
 * @author <a href="mailto:aslak.hellesoy@bekk.no">Aslak Helles</a>
 * @created 1. juli 2002
 * @version $Id: KindFileResourceLoader.java,v 1.1 2005/10/25 14:59:22 lusu Exp $
 */
public class KindFileResourceLoader extends ResourceLoader {

   /** This is used to try to load the templateName as a file */
   FileResourceLoader file = new FileResourceLoader();

   /**
    * This is used to try to load the templateName from the classpath
    */
   ClasspathResourceLoader classpath = new ClasspathResourceLoader();


   /**
    * Get an InputStream so that the Runtime can build a template with it.
    *
    * @param templateName the name of the template
    * @return InputStream containing the template, or an empty steam if not
    *      found.
    * @throws ResourceNotFoundException if template not found in the file
    *      template path.
    */
   public synchronized InputStream getResourceStream(String templateName)
          throws ResourceNotFoundException {

      InputStream retVal = null;
      try {
         retVal = file.getResourceStream(templateName);
      } catch (ResourceNotFoundException e) {
         rsvc.info("KindFileResourceLoader : file loader failed to load: "
               + templateName);
         try {
            // Now try the classpath loader, for some reason when it fails to
            // locate a file it returns null instead of throwing an exception
            retVal = classpath.getResourceStream(templateName);
         } catch (ResourceNotFoundException re) {
            rsvc.info("KindFileResourceLoader : classpath loader failed to load: "
                  + templateName);
         }
      }
      if (retVal == null) {
         retVal = new ByteArrayInputStream(new byte[0]);
      }
      return retVal;
   }


   /**
    * Gets the SourceModified attribute of the KindFileResourceLoader object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param resource Describe what the parameter does
    * @return The SourceModified value
    */
   public boolean isSourceModified(Resource resource) {
      // We need to fix this to check both resourceLoaders
      return file.isSourceModified(resource) ||
            classpath.isSourceModified(resource);
   }


   /**
    * Gets the LastModified attribute of the KindFileResourceLoader object
    *
    * @todo-javadoc Write javadocs for method parameter
    * @param resource Describe what the parameter does
    * @return The LastModified value
    */
   public long getLastModified(Resource resource) {
      // ClasspathResourceLoader always returns 0 so the addition is not really
      // needed but it doesn't hurt.
      return file.getLastModified(resource) + classpath.getLastModified(resource);
   }


   /**
    * Describe what the method does
    *
    * @todo-javadoc Write javadocs for method
    * @todo-javadoc Write javadocs for method parameter
    * @param configuration Describe what the parameter does
    */
   public void init(ExtendedProperties configuration) {
      file.commonInit(rsvc, configuration);
      file.init(configuration);
      classpath.commonInit(rsvc, configuration);
      classpath.init(configuration);
   }

}
