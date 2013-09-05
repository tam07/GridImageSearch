package com.codepath.gridimagesearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// to get rid of warning after implementing Serializable, hover over "ImageResult" and select add generated serial version ID
public class ImageResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2636389073439281291L;
	private String fullUrl;
	private String thumbUrl;
	
	public String getFullUrl() {
		return fullUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}
	
	public ImageResult(JSONObject json) {
		try {
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
		}
		catch (JSONException e) {
			this.fullUrl = null;
			this.thumbUrl = null;
		}
	}

	public String toString() {
		return thumbUrl;
	}
	/* Transforming JSONArray into ArrayList of ImageResult(our model) */
	public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for(int x= 0; x < array.length(); x++) {
			try {
				results.add(new ImageResult(array.getJSONObject(x)));
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
}
