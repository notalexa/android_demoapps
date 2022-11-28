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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import not.alexa.challenge.fortune.R;


public class ChooseView<T extends ChooseProvider.Entry> extends ListView {

	public ChooseView(Context context) {
		super(context);
	}

	public ChooseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChooseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setProvider(ChooseProvider<T> provider) {
		Adapter<T> adapter=new Adapter<T>(getContext(),provider);
		setAdapter(adapter);
	}
	
	private class Adapter<T extends ChooseProvider.Entry> extends ArrayAdapter<T> {
    	private ChooseProvider<T> provider;
    	private LayoutInflater inflater=LayoutInflater.from(getContext());
		public Adapter(Context context, ChooseProvider<T> provider) {
			super(context, -1, provider.getEntries());
			this.provider=provider;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=convertView==null?inflater.inflate(provider.getEntryView(),parent,false):convertView;
			final T entry=getItem(position);
			CharSequence label=entry.getLabel(getContext());
			((TextView)view.findViewById(R.id.label)).setText(label==null?"":label);
			Drawable logo=entry.getLogo(getContext());
			ColorStateList list=view.getBackgroundTintList();
			View logoView=view.findViewById(R.id.logo);
			if(logo!=null) {
				((ImageView)logoView).setImageDrawable(logo);
				logoView.setVisibility(View.VISIBLE);
			} else {
				logoView.setVisibility(View.INVISIBLE);
			}
			Checkable enabledView=view.findViewById(R.id.enabled);
			boolean checked=provider.isEntryEnabled(entry);
			((View)enabledView).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					provider.onEntryChanged(entry,enabledView.isChecked());
					notifyDataSetChanged();
				}
			});
			((Checkable)enabledView).setChecked(checked);
			return view;
		}
    }
}
