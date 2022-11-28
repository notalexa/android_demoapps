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
package not.alexa.challenge.fortune.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class FortunePreferences implements Parcelable {
    protected FortuneMetaData[] metaData;
    protected FortunePreferences(Parcel in) {
        Parcelable[] r=in.readParcelableArray(FortuneMetaData.class.getClassLoader());
        metaData=new FortuneMetaData[r.length];
        System.arraycopy(r,0,metaData,0,r.length);
    }

    public FortunePreferences(FortuneMetaData...metaData) {
        this.metaData=metaData;
    }

    public FortuneMetaData[] getMetaData() {
        return metaData;
    }

    public FortuneMetaData[] getEnabledMetaData() {
        FortuneMetaData[] enabled=metaData.clone();
        int j=0;
        for(int i=0;i<enabled.length;i++) {
            if(enabled[i].isEnabled()&&i!=j) {
                enabled[j]=enabled[i];
                j++;
            }
        }
        return Arrays.copyOf(enabled,j);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(metaData,0);
    }

    public FortunePreferences copyInto(FortunePreferences outer) {
        outer.metaData=metaData;
        return outer;
    }

    public synchronized FortunePreferences add(FortuneMetaData meta) {
        for(int i=0;i<metaData.length;i++) {
            if(metaData[i].getFile().equals(meta.getFile())) {
                meta.setEnabled(metaData[i].isEnabled());
                metaData[i]=meta;
                return this;
            }
        }
        FortuneMetaData[] n= Arrays.copyOf(metaData,metaData.length+1);
        n[metaData.length]=meta;
        metaData=n;
        return this;
    }

    public String toString() {
        return "Prefs"+Arrays.toString(metaData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FortunePreferences> CREATOR = new Creator<FortunePreferences>() {
        @Override
        public FortunePreferences createFromParcel(Parcel in) {
            return new FortunePreferences(in);
        }

        @Override
        public FortunePreferences[] newArray(int size) {
            return new FortunePreferences[size];
        }
    };
}
