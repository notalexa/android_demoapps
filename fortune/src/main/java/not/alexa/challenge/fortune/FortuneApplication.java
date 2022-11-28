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
package not.alexa.challenge.fortune;

import android.app.Application;
import android.os.Environment;
import android.service.trust.AuthLevel;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import not.alexa.challenge.fortune.data.FortuneMetaData;
import not.alexa.challenge.fortune.data.FortuneStore;
import not.alexa.challenge.fortune.data.FortuneDataHandler;

public class FortuneApplication extends Application {
    static String TAG="FortuneApplication";
    private File dataDir;
    private FortuneDataHandler reader;
    private FortuneStore store;
    private Random random=new SecureRandom();

    @Override
    public void onCreate() {
        super.onCreate();
        dataDir=getDir("data",MODE_PRIVATE);
        reader=new FortuneDataHandler(this,getAssets(),dataDir);
        Log.i(TAG,"Create Application:  datadir="+dataDir.getAbsolutePath());
        init();
    }

    protected void init() {
        List<String> outdated=reader.outdated();
        store=reader.getPreferences();
        if(outdated!=null&&outdated.size()>0) for(String f:outdated) {
            Log.i(TAG,"File "+f+" is outdated");
            store.add(reader.makeData(f));
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public String getFortune() {
        return store.getFortune(random);
    }

    public List<FortuneMetaData> getCollections() {
        List<FortuneMetaData> collections=store.getCollections();
        Collections.sort(collections,FortuneMetaData.COMPARATOR);
        return collections;
    }

    public void setEnabled(FortuneMetaData metaData,boolean enabled) {
        Log.i(TAG,"Set metadata for "+metaData.getFile()+" enabled="+enabled);
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        store.setEnabled(metaData,enabled);
    }

    public int getChallengeCount() {
        return store.getCount();
    }
}
