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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class FortuneData implements Parcelable {
    protected String title;
    protected String[] content;
    
    public FortuneData(String title,Collection<String> content) {
        this.title=title;
        this.content=content.toArray(new String[content.size()]);
    }

    protected FortuneData(Parcel in) {
        title = in.readString();
        in.readStringArray(content=new String[in.readInt()]);
    }

    public static final Creator<FortuneData> CREATOR = new Creator<FortuneData>() {
        @Override
        public FortuneData createFromParcel(Parcel in) {
            return new FortuneData(in);
        }

        @Override
        public FortuneData[] newArray(int size) {
            return new FortuneData[size];
        }
    };

    public String getTitle() {
        return title;
    }
    
    public int getCount() {
        return content==null?0:content.length;
    }
    
    public String getFortune(Random random) {
        if(content!=null&&content.length>0) {
            return content[random.nextInt(content.length)];
        } else {
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(content.length);
        dest.writeStringArray(content);
    }
}
