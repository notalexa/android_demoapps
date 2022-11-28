/*
 * Copyright (C) 2022 Not Alexa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package not.alexa.challenge.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.List;

public interface ChooseProvider<T extends ChooseProvider.Entry> {

	public List<T> getEntries();
	public int getEntryView();
	public boolean isEntryEnabled(T entry);
	public void onEntryChanged(T entry, boolean enabled);
	
	public interface Entry {
		CharSequence getLabel(Context context);
		Drawable getLogo(Context context);
	}
}
