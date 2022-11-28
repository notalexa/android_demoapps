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
import android.content.res.AssetManager;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import not.alexa.challenge.fortune.R;

public class FortuneDataHandler {
    private static final Map<String,Integer> TEXT_MAP=new HashMap<>();
    static {
        TEXT_MAP.put("de",R.string.de);
        TEXT_MAP.put("en",R.string.en);
    }
    protected AssetManager textFiles;
    protected File dataFiles;
    private Map<String,FortuneData> loaded=new HashMap<>();
    private FortuneStore fortune;
    private Context context;
    public FortuneDataHandler(Context context, AssetManager textFiles, File dataFiles) {
        this.context=context;
        this.textFiles=textFiles;
        this.dataFiles=dataFiles;
    }
    
    File getDataFile(String name) {
        return new File(dataFiles,name.substring(0,name.length()-3)+".dat");
    }

    public List<String> outdated() {
        if(dataFiles.list().length>0) {
            return Collections.emptyList();
        }
        return outdated("/");

    }
    private List<String> outdated(String dir) {
        try {
            String d="fortune"+dir;
            String[] files = textFiles.list(d);//new File(textFiles,dir).listFiles();
            List<String> outDated = new ArrayList<>();
            if (files != null) for (String f : files) {
                if(f.endsWith(".u8")) {
                    outDated.add(dir+f);
                } else if(f.indexOf('.')<0) {
                    outDated.addAll(outdated(dir + f+"/"));
                }
            }
            return outDated;
        } catch(Throwable t) {
            return Collections.emptyList();
        }
    }

    public FortuneMetaData makeData(String f) {
        String index=f.indexOf('/',1)>0?f.substring(1,f.lastIndexOf('/')):"";
        MessageFormat titleFormat=new MessageFormat("{0}");
        if(TEXT_MAP.containsKey(index)) {
            titleFormat=new MessageFormat(context.getText(TEXT_MAP.get(index)).toString());
        }
        File outFile=getDataFile(f);
        List<String> content=new ArrayList<String>();
        String title=f.substring(index.length()+1,f.length()-3);
        int cmd=0;
        try(BufferedReader reader=new BufferedReader(new InputStreamReader(textFiles.open("fortune"+f),Charset.forName("UTF-8")))) {
            String line;
            String quote="";
            while((line=reader.readLine())!=null) {
                if("%".equals(line)) {
                    if(quote.length()>0) {
                        switch(cmd) {
                        case 0:content.add(quote.trim()+"\n");
                            break;
                        case 1:title=titleFormat.format(new Object[] {quote.trim()});
                            break;
                        }
                        cmd=0;
                    }
                    quote="";
                } else if("%%TITLE%%".equals(line.trim())) {
                    cmd=1;
                } else {
                    if(cmd==0) {
                        if(line.trim().length()==0&&!quote.endsWith("\n")) {
                            // Empty line is a separator
                            quote+="\n";
                            continue;
                        }
                        if((line.startsWith("\t")||line.startsWith("  "))&&!quote.endsWith("\n")) {
                            line="\n" + line;
                        } else {
                            // Append to line with space
                            if(quote.length()>0) {
                                line=" "+line;
                            }
                            if (line.endsWith("!") ||line.endsWith("?") || line.endsWith(".")) {
                                line+="\n";
                            }
                        }
                    }
                    quote+=line;
                }
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
        outFile.getParentFile().mkdirs();
        if(outFile.getParentFile().exists()) {
            Parcel parcel= Parcel.obtain();
            parcel.writeParcelable(new FortuneData(title,content),0);
            try (OutputStream out = new FileOutputStream(outFile)) {
                out.write(parcel.marshall());
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                parcel.recycle();
            }
        }
        loaded.remove(outFile.getName());
        return new FortuneMetaData(f.substring(0,f.length()-3)+".dat",title,false,content.size());
    }

    public FortuneStore getPreferences() {
        if(fortune==null) {
            synchronized (this) {
                if(fortune==null) {
                    final File prefFile=new File(dataFiles,"fortune.prefs");
                    fortune=new FortuneStore() {
                        FortuneMetaData[] enabledMetaData;
                        int count;
                        FortunePreferences prefs=load(prefFile,new FortunePreferences());
                        @Override
                        public FortuneStore add(FortuneMetaData data) {
                            prefs.add(data);
                            store();
                            return this;
                        }

                        protected void store() {
                            Parcel parcel= Parcel.obtain();
                            parcel.writeParcelable(prefs,0);
                            Log.i("FortunePrefs","Write to "+prefFile.getAbsolutePath());
                            try(OutputStream out=new FileOutputStream(prefFile)) {
                                out.write(parcel.marshall());
                            } catch(Throwable t) {
                                t.printStackTrace();
                            } finally {
                                parcel.recycle();
                            }
                            enabledMetaData=null;
                        }

                        @Override
                        public List<FortuneMetaData> getCollections() {
                            return Arrays.asList(prefs.getMetaData());
                        }

                        @Override
                        public void setEnabled(FortuneMetaData data, boolean enabled) {
                            boolean old=data.isEnabled();
                            if(old!=enabled) {
                                data.setEnabled(enabled);
                                store();
                            }
                        }

                        @Override
                        public int getCount() {
                            getEnabledMetaData();// Side effect: load and set count if necessary.
                            return count<=0?1:count;
                        }

                        private synchronized FortuneMetaData[] getEnabledMetaData() {
                            if(enabledMetaData==null) {
                                FortuneMetaData[] meta=prefs.getEnabledMetaData();
                                count=0;
                                for(FortuneMetaData m:meta) {
                                    count += m.getCount();
                                }
                                enabledMetaData=meta;
                                Log.i("FortuneReader","Set Metadata to "+Arrays.toString(meta));
                            }
                            return enabledMetaData;
                        }

                        @Override
                        public String getFortune(Random random) {
                            FortuneMetaData[] meta=getEnabledMetaData();//enabledMetaData;//prefs.getMetaData();
                            if(meta.length>0) {
                                FortuneMetaData m=meta[random.nextInt(meta.length)];
                                FortuneData data=getFortuneData(m);
                                String s=data.getFortune(random);
                                return s;
                            }
                            return context.getString(R.string.default_fortune);//"Eine Welt ohne Fortune ist wie ein Ritt ohne Pferd.\n    -- Not Alexa";
                        }

                        @NonNull
                        @Override
                        public String toString() {
                            return prefs.toString();
                        }
                    };
                }
            }
        }
        return fortune;
    }

    public FortuneData getFortuneData(FortuneMetaData metaData) {
        System.out.println("Get data for "+metaData.getFile());
        return getFortuneData(metaData.getFile());
    }

    public FortuneData getFortuneData(String dataFile) {
        return getFortuneData(new File(dataFiles,dataFile));
    }

    protected <T> T load(File dataFile,T defaultValue) {
        Parcel parcel=Parcel.obtain();
        if(dataFile.exists()) try {
            byte[] dataBytes=Files.readAllBytes(dataFile.toPath());
            parcel.unmarshall(dataBytes,0,dataBytes.length);
            parcel.setDataPosition(0);
            Object o=parcel.readParcelable(FortuneDataHandler.class.getClassLoader());
            if(o!=null) {
                return (T)o;
            }
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            parcel.recycle();
        }
        return defaultValue;
    }

    public FortuneData getFortuneData(File dataFile) {
        FortuneData data=loaded.get(dataFile.getName());
        if(data!=null) {
            return data;
        }
        data=load(dataFile,new FortuneData(dataFile.getName(), Collections.<String>emptySet()));
        loaded.put(dataFile.getName(),data);
        return data;
    }
}
