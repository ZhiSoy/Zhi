/*
 * Copyright [2015] [zhi]
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
 */
package com.zhi.common;

import android.content.Context;
import android.content.SharedPreferences;

public interface ApplicationContext {
    /**
     * Return the context of the single, global Application object of the
     * current process.  This generally should only be used if you need a
     * Context whose lifecycle is separate from the current context, that is
     * tied to the lifetime of the process rather than the current component.
     *
     * @see android.app.Activity#getApplicationContext()
     */
    Context getApplicationContext();

    /**
     * Retrieve a {@link SharedPreferences} object for accessing preferences
     * that are private to this application.
     *
     * @return Returns the single SharedPreferences instance that can be used
     * to retrieve and modify the preference values.
     */
    SharedPreferences getPreferences();
}
