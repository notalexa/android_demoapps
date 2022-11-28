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
package not.alexa.challenge.fortune.settings;

import java.util.List;

import not.alexa.challenge.fortune.FortuneApplication;
import not.alexa.challenge.fortune.R;
import not.alexa.challenge.fortune.data.FortuneMetaData;
import not.alexa.challenge.view.ChooseProvider;

public class FortuneMetaDataProvider implements ChooseProvider<FortuneMetaData> {
    FortuneApplication app;
    public FortuneMetaDataProvider(FortuneApplication app) {
        this.app=app;
    }

    @Override
    public List<FortuneMetaData> getEntries() {
        return app.getCollections();
    }

    @Override
    public int getEntryView() {
        return R.layout.chooser_entry;
    }

    @Override
    public boolean isEntryEnabled(FortuneMetaData entry) {
        return entry.isEnabled();
    }

    @Override
    public void onEntryChanged(FortuneMetaData entry,boolean enabled) {
        app.setEnabled(entry,enabled);
    }
}
