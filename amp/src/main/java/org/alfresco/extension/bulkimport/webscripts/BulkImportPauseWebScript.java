/*
 * Copyright (C) 2007-2016 Peter Monks.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This file is part of an unsupported extension to Alfresco.
 * 
 */

package org.alfresco.extension.bulkimport.webscripts;


import org.alfresco.extension.bulkimport.BulkImporter;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Web Script class that pauses a bulk import, if one is in progress.
 *
 * @author Peter Monks (peter.monks@alfresco.com)
 */
public class BulkImportPauseWebScript
    extends DeclarativeWebScript
{
    private final BulkImporter importer;


    public BulkImportPauseWebScript(final BulkImporter importer)
    {
        // PRECONDITIONS
        assert importer != null : "importer must not be null.";

        // BODY
        this.importer = importer;
    }


    /**
     * @see DeclarativeWebScript#executeImpl(WebScriptRequest, Status, Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest request, final Status status, final Cache cache)
    {
        Map<String, Object> result = new HashMap<>();

        cache.setNeverCache(true);
        
        if (importer.getStatus().inProgress())
        {
            if (!importer.getStatus().isPaused())
            {
                result.put("result", "pause requested");
                importer.pause();
                status.setCode(Status.STATUS_ACCEPTED, "Pause requested.");
                status.setRedirect(true);  // Make sure the custom 202 status template is used (why this is needed at all is beyond me...)
            }
            else
            {
                result.put("result", "import is already paused");
                status.setCode(Status.STATUS_BAD_REQUEST, "Bulk import is already paused.");
            }
        }
        else
        {
            result.put("result", "no imports in progress");
            status.setCode(Status.STATUS_BAD_REQUEST, "No bulk imports are in progress.");
        }
        
        return(result);
    }
}
