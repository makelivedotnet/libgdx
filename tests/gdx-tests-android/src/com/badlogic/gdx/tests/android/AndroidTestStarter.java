/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.badlogic.gdx.tests.android;

import java.util.ArrayList;

import com.badlogic.gdx.tests.utils.GdxTests;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidTestStarter extends ListActivity {

	@Override public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<String> testNames = new ArrayList<String>();
		for(String name: GdxTests.getNames()) {
			testNames.add(name);
		}		
		testNames.add(MatrixTest.class.getSimpleName());		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testNames.toArray(new String[0])));
	}

	protected void onListItemClick (ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object o = this.getListAdapter().getItem(position);
		String testName = o.toString();

		Bundle bundle = new Bundle();
		bundle.putString("test", testName);
		Intent intent = new Intent(this, GdxTestActivity.class);
		intent.putExtras(bundle);

		startActivity(intent);
	}

}
