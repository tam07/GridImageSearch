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
    
    static final int REQUEST_CODE = 100;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	/*TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);*/
    	
    	if (resultCode == RESULT_OK && requestCode == REQUEST_CODE)
    	{
    		/* GETTING search options from SearchOptionsActivity bundle */
            // don't use getIntent().getStringExtra
    		imgSize = data.getStringExtra("selectedImgSize");
    		color = data.getStringExtra("selectedColor");
    		imgType = data.getStringExtra("selectedImgType");
    		site = data.getStringExtra("writtenSite");
    	}
    }
    
	
	public void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnLoadMore = (Button) findViewById(R.id.btnLoadMore);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
				
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				// INTENT is a request to bring up an activity.  Second arg is the class of the activity you want to bring up
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("result", imageResult);
				startActivity(i);
			}	
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
	
		return true;
	}
	
	
	// THROUGH THE XML, android:onClick in menu dir-->search.xml
	public void onSettingsAction(MenuItem mi) {
		/*Intent i = new Intent(getApplicationContext(), SearchOptionsActivity.class);
		startActivity(i);*/
		Intent i = new Intent(this, SearchOptionsActivity.class);
		i.putExtra("mode", 2);
		startActivityForResult(i, REQUEST_CODE);
	}
	
	
	// Event handled by assoc layout's Search button via onClick.  This method display images starting at index 0 ALWAYS
	public void onImageSearch(View v) {
		/* the first time search is pressed, this variable doesn't matter.  But if LoadMore
		 * was pressed, then search is pressed after, this resets us to have LoadMore
		 * work/paginate correctly in the future
		 */
		numImgsLoaded = 0;		

		// get the request str, specify that a search, not a "load more" was performed
		String requestStr = getRequestStringHelper(false);
		System.out.println("IN onImageSearch EVENT HANDLER, directed by xml onClick: " + requestStr);
		loadImagesHelper(requestStr);
	} 
	
	
	/* Event handled by assoc layout's "Load More" button via onClick.  Displays the next 8 images, "paginating" our 
	 * results
	 */
	public void onLoadMoreImages(View v) {
		// user presses Load More without a Search Press
		if(numImgsLoaded == 0)
			onImageSearch(v);
		// otherwise search has been pressed at least once
		else
		{
			String requestStr = getRequestStringHelper(true);
			System.out.println("IN onLoadMoreImages EVENT HANDLER, directed by xml onClick: " + requestStr);
			loadImagesHelper(requestStr);
		}	
	}
	 
	public String getRequestStringHelper(boolean fromOnLoadMore)
	{
		String gSearchStr = etQuery.getText().toString();
		String encodedSearchTerm = Uri.encode(gSearchStr);
		String functionality = "";
		
		if(fromOnLoadMore)
		    functionality = "Loading";
		else
			functionality = "Searching";
		
		Toast.makeText(this, functionality + " " + gSearchStr, Toast.LENGTH_SHORT).show();
		
		String requestStr = "https://ajax.googleapis.com/ajax/services/search/images?rsz=" + NUM_RESULTS + 
				            "&start=" + numImgsLoaded + "&v=1.0";
		
		/* if search filter activity was not visited, imgSize is null.  Otherwise, if it was visited and if any filters
		 * were specified, add them to the request string.  add "&imgsz=<imgSize>" to the request string */
		if(imgSize != null && !imgSize.equals(""))
		    requestStr+= "&imgsz=" + imgSize;
		if(color != null && !color.equals(""))
			requestStr+="&imgcolor=" + color;
		if(imgType != null && !imgType.equals(""))
			requestStr+="&imgtype=" + imgType;
		if(site != null && site != "")
			requestStr+="&as_sitesearch=" + site;
		
		// append request string with google search term
		requestStr+="&q=" + encodedSearchTerm;
		
		return requestStr;
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
