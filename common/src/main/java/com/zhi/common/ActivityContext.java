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

public interface ActivityContext {
    /**
     * Returns the context associated with the components(Activity, Dialogs, Fragment, View and so on).
     * This is different from the value returned by {@link android.app.Activity#getApplicationContext()},
     * which is the single context of the root activity. Some components (dialogs) require the context
     * of the activity. When implementing this within an activity, you can return 'this', since each
     * activity is also a context.
     *
     * @return the context associated with this activity.
     */
    Context getActivityContext();
}
