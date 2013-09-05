package com.codepath.gridimagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
    ImageResultArrayAdapter imageAdapter;
    
    String imgSize;
    
    String color;
    String imgType;
    String site;
    
    Button btnLoadMore;
    
    int numImgsLoaded;
    
    static final int NUM_RESULTS = 8;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		
		//imgSize = getIntent().getStringExtra("imageSize");
		/* GETTING search options from SearchOptionsActivity bundle */
		imgSize = getIntent().getStringExtra("selectedImgSize");
		color = getIntent().getStringExtra("selectedColor");
		imgType = getIntent().getStringExtra("imgType");
		site = getIntent().getStringExtra("writtenSite");
		
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				// INTENT is a request to bring up an activity.  Second arg is the class of the activity you want to bring up
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				//i.putExtra("url", imageResult.getFullUrl());
				i.putExtra("result", imageResult);
				// fire intent, pass in extras
				startActivity(i);
				// CODING INSTRUCTIONS: now go to activity you want to display, ImageDisplayActivity
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
	
		return true;
	}
	
	public void onSettingsAction(MenuItem mi) {
		Intent i = new Intent(getApplicationContext(), SearchOptionsActivity.class);
		startActivity(i);

	}
	
	public void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnLoadMore = (Button) findViewById(R.id.btnLoadMore);
	}
	
	// display images starting at index 0 ALWAYS
	public void onImageSearch(View v) {
		/* the first time search is pressed, this variable doesn't matter.  But if LoadMore
		 * was pressed, then search is pressed after, this resets us to have LoadMore
		 * work/paginate correctly in the future
		 */
		numImgsLoaded = 0;
		String query = etQuery.getText().toString();
		String encodedSearchTerm = Uri.encode(query);
		Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
		
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android gives a proper response
		
		String requestStr = "https://ajax.googleapis.com/ajax/services/search/images?rsz=" + NUM_RESULTS + "&start=0&v=1.0";
		// if one of the options wasn't specified in SearchOptionsActivity, use default req str
		if(imgSize == null || color == null || imgType == null || site == null)
		{
			 requestStr+= "&q=" + encodedSearchTerm;
		}
		else
		{
		    String imgSizeAttr = "&imgsz=" + imgSize;
		    String imgColorAttr = "&imgcolor=" + color;
		    String imgTypeAttr = "&imgtype=" + imgType;
	        String siteAttr = "&as_sitesearch=" + site;
	        requestStr+= imgSizeAttr + imgColorAttr + imgTypeAttr + siteAttr + "&q=" + encodedSearchTerm;
		}
		
		/* gives null response
		 * String requestStr = "https://ajax.googleapis.com/ajax/services/search/images?rsz=&&start=0&v=1.0&q=" + encodedSearchTerm;
		 */
		/*String requestStr = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&start=0&v=1.0&imgsz=" +
		                    imgSize + "&as_sitesearch=flickr.com&q=" + encodedSearchTerm;*/
		
		
		
		numImgsLoaded+=NUM_RESULTS;
	} //close onImageSearchView
	
	
	// displays the next 8 images, "paginating" our results after pressing Load More
	public void onLoadMoreImages(View v) {
		String query = etQuery.getText().toString();
		String encodedSearchTerm = Uri.encode(query);
		Toast.makeText(this, "Loading " + query, Toast.LENGTH_SHORT).show();
		// user presses Load More without a Search Press
		if(numImgsLoaded == 0)
			onImageSearch(v);
		// otherwise search has been pressed at least once
		else
		{
			String requestStr = "https://ajax.googleapis.com/ajax/services/search/images?rsz=" +
		                        NUM_RESULTS + "&start=" + numImgsLoaded + "&v=1.0";
			loadImagesHelper(requestStr);
		}	
	}
	 
	
	/* sends the http GET and displays the images, the type of images returned will depend
	 * on whether filters were set or not(as seen in the requestStr)
	 */
	public void loadImagesHelper(String requestStr)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(requestStr, 
		           new JsonHttpResponseHandler() {
			       @Override
			       public void onSuccess(JSONObject response) {
			    	   JSONArray imageJsonResults = null;
			    	   try {
			    		   // results is the array of data we want, visit the url to see what you want
			    		   imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
			    		   imageResults.clear();
			    		   /* ImageResult is our model.  Get array of ImageResult by calling fromJSONArray(JSONArray imageJsonResults)
			    		    * 
			    		    * It is a common PATTERN to have the model parse the JSONArray
			    		    * Load data into:
			    		    * 1) the array
			    		    * 2) the grid
			    		    * 3) notify the adapter
			    		    */
			    		   imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
			    		   Log.d("DEBUG", imageResults.toString());
			    	   }
			    	   catch (JSONException e) {
			    		   e.printStackTrace();
			    	   }
			       }
		});// close anonymous class JsonHttpResponseHandler
		// update running total of images loaded so far
		numImgsLoaded+=NUM_RESULTS;
	}
	
}
