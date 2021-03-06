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
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A checkbox with a label.
 * 
 * <h2>Functionality</h2>
 * A CheckBox can be either checked or unchecked. The state of the CheckBox is displayed
 * with images representing the checked and unchecked state as well as a label, displayed to the right of the
 * image(s). Checking/unchecking a CheckBox works by clicking either the image or the label itself, the hit
 * area of the CheckBox is the compositum of both elements. </p>
 * 
 * To get notifications about a change in state of the CheckBox one can register a {@link CheckedListener} with
 * the CheckBox.</p>
 * 
 * <h2>Layout</h2>
 * A CheckBox's (preferred) width and height is determined by the size of the bigger of the two images representing
 * the state plus the bounding box around the (single-line) label. Both the image and the label are centered vertically.
 * Use {@link CheckBox#setPrefSize(int, int)} to programmatically change the size to your liking. Note that this
 * will not have an affect on the actual rendering, but only manipulate the hit area of the CheckBox.
 * 
 * <h2>Style</h2>
 * A CheckBox is a {@link Widget} displaying one of two {@link TextureRegion} instances representing the checked and
 * unchecked state, as well as a label via a {@link BitmapFont} and accompanying {@link Color}. The style is defined
 * via an instance of {@link CheckBoxStyle}, which can either be done programmatically or via a {@link Skin}.</p>
 * 
 * A CheckBox's style definition in a skin XML file should look like this:
 * 
 *  <pre>
 *  {@code 
 *  <checkbox name="styleName"
 *            checked="checkedRegion" 
 *            unchecked="uncheckedRegion"
 *            font="fontName" 
 *            fontColor="fontColor"/>
 *  }
 *  </pre>
 *  
 * <ul>
 * <li>The <code>name</code> attribute defines the name of the style which you can later use with {@link Skin#newCheckBox(String, String, String)}.</li>
 * <li>The <code>checked</code> attribute references a {@link TextureRegion} by name, to be used when the CheckBox is checked</li>
 * <li>The <code>unchecked</code> attribute references a {@link TextureRegion} by name, to be used when the CheckBox is unchecked</li>
 * <li>The <code>font</code> attribute references a {@link BitmapFont} by name, to be used to render the label</li>
 * <li>The <code>fontColor</code> attribute references a {@link Color} by name, to be used to render the label</li>
 * </ul> 
 *  
 * @author mzechner
 *
 */
public class CheckBox extends Widget {
	final CheckBoxStyle style;
	String label;	
	boolean isChecked = false;
	CheckedListener listener = null;
	
	final Vector2 boxPos = new Vector2();
	final Vector2 textPos = new Vector2();
	final TextBounds textBounds = new TextBounds();
	final Rectangle bounds = new Rectangle();
	float checkWidth = 0;
	float checkHeight = 0;	
	
	/**
	 * Creates a new check box. The width and height of the check box are 
	 * determined by its label text and style.
	 * @param name the name 
	 * @param label the label
	 * @param style the {@link CheckBoxStyle}
	 */
	public CheckBox(String name, String label, CheckBoxStyle style) {
		super(name, 0, 0);
		this.style = style;
		this.label = label;		
		layout();
		this.width = prefWidth;
		this.height = prefHeight;
	}	

	@Override
	public void layout() {
		final BitmapFont font = style.font;
		final TextureRegion checkedRegion = style.checked;
		final TextureRegion uncheckedRegion = style.unchecked;
		
		textBounds.set(font.getBounds(label));
		textBounds.height -= font.getDescent();
		checkWidth = Math.max(checkedRegion.getRegionWidth(), uncheckedRegion.getRegionWidth());
		checkHeight = Math.max(checkedRegion.getRegionHeight(), uncheckedRegion.getRegionHeight());		
		if(textBounds.height > checkHeight) {
			prefHeight = textBounds.height;
			boxPos.y = (int)((textBounds.height - checkedRegion.getRegionHeight()) / 2);
			textPos.y = textBounds.height;
		} else {
			prefHeight = checkHeight;			
			boxPos.y = 0;
			textPos.y = (int)((checkHeight - textBounds.height) / 2) + textBounds.height;
		}
		
		boxPos.x = 0;
		textPos.x = checkWidth + 5;
		prefWidth = checkWidth + 5 + textBounds.width;
		invalidated = false;
	}

	@Override
	protected void draw(SpriteBatch batch, float parentAlpha) {
		final BitmapFont font = style.font;
		final TextureRegion checkedRegion = style.checked;
		final TextureRegion uncheckedRegion = style.unchecked;
		final Color fontColor = style.fontColor;
		
		if(invalidated) layout();
		
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		if(isChecked) batch.draw(checkedRegion, x + boxPos.x, y + boxPos.y);
		else batch.draw(uncheckedRegion, x + boxPos.x, y + boxPos.y);
		
		font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * parentAlpha);
		font.draw(batch, label, x + textPos.x, y + textPos.y);
	}

	@Override
	protected boolean touchDown(float x, float y, int pointer) {
		return false;
	}

	@Override
	protected boolean touchUp(float x, float y, int pointer) {
		if(x >= 0 && x < width &&
		   y >= 0 && y < height) {
			isChecked = !isChecked;
			if(listener != null) listener.checked(this, isChecked);
		}
		return false;
	}

	@Override
	protected boolean touchDragged(float x, float y, int pointer) {
		return false;
	}

	@Override
	public Actor hit(float x, float y) {
		return x > 0 && x < width && y > 0 && y < height?this: null;
	}	
	
	/**
	 * Interface for listening to check events on this check box.
	 * @author mzechner
	 *
	 */
	public interface CheckedListener {
		public void checked(CheckBox checkBox, boolean isChecked);
	}
	
	/**
	 * Sets the {@link CheckedListener}
	 * @param listener the listener or null
	 * @return this CheckBox for chaining
	 */
	public CheckBox setCheckedListener(CheckedListener listener) {
		this.listener = listener;
		return this;
	}
	
	/**
	 * Defines a check box style, see {@link CheckBox}
	 * @author mzechner
	 *
	 */
	public static class CheckBoxStyle {
		public final BitmapFont font;
		public final Color fontColor;
		public final TextureRegion checked;
		public final TextureRegion unchecked;
		
		public CheckBoxStyle(BitmapFont font, Color fontColor, TextureRegion checked, TextureRegion unchecked) {
			this.font = font;
			this.fontColor = fontColor;
			this.checked = checked;
			this.unchecked = unchecked;
		}
	}

	/**
	 * Sets whether this check box is checked or not. Invalidates
	 * all parents.
	 * @param isChecked checked or not
	 */
	public void setChecked (boolean isChecked) { 
		this.isChecked = isChecked;
		invalidateHierarchy();
	}
	
	/**
	 * @return whether this checkbox is checked or not
	 */
	public boolean isChecked() {
		return isChecked;
	}
}