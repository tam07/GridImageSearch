package com.codepath.gridimagesearch;

import java.util.List;

import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultArrayAdapter(Context context, List<ImageResult> images) {
		/* context, xml that will be translated to an item, source collection
		 * android.R.layout.simple_list_item_1 is xml describing a textview, which can only 
		 * display words, not an image.  
		 * 
		 * Call toString on an ImageResult object.
		 * Add an object of this type(ImageResultArrayAdapter) to our controller(SearchActivity.java)
		 */
		//super(context, android.R.layout.simple_list_item_1, images);
		super(context, R.layout.item_image_result, images);
	}
	
	
	// transform data into view(right now done through imageResult.toString().  We do this properly by overriding getView method
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
		
		ImageResult imageInfo = this.getItem(position);
		SmartImageView ivImage;
		// take R.layout.item_image_result(xml description of item) and INFLATE it into a java object
		if(convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			ivImage = (SmartImageView) inflator.inflate(R.layout.item_image_result, parent, false);
		} else {
			ivImage = (SmartImageView) convertView;
			// clear data that was already there??
			ivImage.setImageResource(android.R.color.transparent);
		}
		ivImage.setImageUrl(imageInfo.getThumbUrl());
		
		return ivImage;
	}
	
	
	
}
