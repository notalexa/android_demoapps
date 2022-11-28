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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

import not.alexa.challenge.view.ChooseProvider;

public class FortuneMetaData implements Parcelable, ChooseProvider.Entry {
    public static Comparator<FortuneMetaData> COMPARATOR=new Comparator<FortuneMetaData>() {
        @Override
        public int compare(FortuneMetaData o1, FortuneMetaData o2) {
            return o1.getLabel(null).toString().compareTo(o2.getLabel(null).toString());
        }
    };
    private String file;
    private String title;
    private boolean enabled;
    private int count;

    public FortuneMetaData(String file,String title,boolean enabled,int count) {
        this.file=file;
        this.title=title;
        this.enabled=enabled;
        this.count=count;
    }

    protected FortuneMetaData(Parcel in) {
        file=in.readString();
        title=in.readString();
        enabled=in.readBoolean();
        count=in.readInt();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int hashCode() {
        return file.hashCode();
    }

    public boolean equals(Object other) {
        if(other instanceof FortuneMetaData) {
            return ((FortuneMetaData)other).file.equals(file);
        }
        return false;
    }

    public String getFile() {
        return file;
    }

    public String toString() {
        return file+"["+enabled+"]: "+title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(file);
        dest.writeString(title);
        dest.writeBoolean(enabled);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FortuneMetaData> CREATOR = new Creator<FortuneMetaData>() {
        @Override
        public FortuneMetaData createFromParcel(Parcel in) {
            return new FortuneMetaData(in);
        }

        @Override
        public FortuneMetaData[] newArray(int size) {
            return new FortuneMetaData[size];
        }
    };

    @Override
    public CharSequence getLabel(Context context) {
        return title==null||title.length()==0?getFile():title;
    }

    @Override
    public Drawable getLogo(Context context) {
        return null;
    }

    public int getCount() {
        return count;
    }
}
