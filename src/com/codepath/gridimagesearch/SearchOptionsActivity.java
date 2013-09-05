package com.codepath.gridimagesearch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchOptionsActivity extends Activity implements OnItemSelectedListener, OnEditorActionListener, OnClickListener {
	private Spinner imgSizeSpinner;
	private Spinner colorSpinner;
	private Spinner imgTypeSpinner;
	
	private TextView siteTextField;
	
	// values obtained from spinners
	private String selectedImgSize;
	private String selectedColor;
    private String selectedImgType;
    
    // value obtained from textfield
    private String selectedSite;
    
    private Button saveButton;
    
    // imgSize obtained by implementing OnItemSelectedListener's onItemSelected(...) method 
    public void setSelectedImgSize(Object imgSize) {
    	selectedImgSize = (String) imgSize;
    }
    
    public String getSelectedImgSize() {
		return selectedImgSize;
	}

    // color obtained by implementing OnItemSelectedListener's onItemSelected(...) method 
    public void setSelectedColor(Object color) {
    	selectedColor = (String) color;
    }
    
	public String getSelectedColor() {
		return selectedColor;
	}
	
	// imgType obtained by implementing OnItemSelectedListener's onItemSelected(...) method
	public void setSelectedImgType(Object imgType) {
		selectedImgType = (String) imgType;
	}
	
	public String getSelectedImgType() {
		return selectedImgType;
	}

	public String getSelectedSite() {
		return selectedSite;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_options);
		
		/* 1.  Get spinner object from the associated layout
		 * 2.  Declare adapter
		 * 3.  Use adapter to call setDropDownViewResource(<layout_type>)
		 * 4.  Use spinner to set this adapter
		 * 5.  attach listener to spinner.  Works because this class implements the
		 *     listener interface, and you define those listener methods in this class
		 *     So pass in "this" to setOnItemSelectedListener, since this class implements
		 *     OnItemSelectedListener
		 */
		imgSizeSpinner = (Spinner)findViewById(R.id.imgSizeSpinner_ID);
		ArrayAdapter<CharSequence> imgSizeAdapter = ArrayAdapter.createFromResource(this, 
				                                    R.array.imgSize, 
				                                    android.R.layout.simple_spinner_item);
		imgSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		imgSizeSpinner.setAdapter(imgSizeAdapter);
		imgSizeSpinner.setOnItemSelectedListener(this);
		
		colorSpinner = (Spinner)findViewById(R.id.colorSpinner_ID);
		ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, 
				                                  R.array.color, 
				                                  android.R.layout.simple_spinner_dropdown_item);
		colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		colorSpinner.setAdapter(colorAdapter);
		colorSpinner.setOnItemSelectedListener(this);

		imgTypeSpinner = (Spinner)findViewById(R.id.imgTypeSpinner_ID);
		ArrayAdapter<CharSequence> imgTypeAdapter = ArrayAdapter.createFromResource(this, 
				                                  R.array.imgType, 
				                                  android.R.layout.simple_spinner_dropdown_item);
		imgTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		imgTypeSpinner.setAdapter(imgTypeAdapter);
		imgTypeSpinner.setOnItemSelectedListener(this);
		
		siteTextField = (EditText)findViewById(R.id.siteTextField_ID);
        siteTextField.setOnEditorActionListener(this);
		
		saveButton = (Button)findViewById(R.id.saveButtonID);
		saveButton.setOnClickListener(this);
		
      
	}

	/* WAS IN onCreate, this is another way to create listener functionality, though an 
	 * inner class:
	 * 
	 * imgSizeSpinner.setOnItemSelectedListener(
	                              new AdapterView.OnItemSelectedListener() {
                   public void onItemSelected(AdapterView<?> parent, View view, int pos,
        		                              long id) {
                       Object item = parent.getItemAtPosition(pos);
                   }
                   public void onNothingSelected(AdapterView<?> parent) {
                   }
               });*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_options, menu);
		return true;
	}

	/* once a spinner value is selected, let this activity store it in memory */
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
		//Object item = parent.getItemAtPosition(position);
		/* Find associated spinner and call that setter with the value selected */
		if(parent.getId() == imgSizeSpinner.getId())
		{
		    String imgSizeStr = parent.getItemAtPosition(position).toString();
			setSelectedImgSize(imgSizeStr);
		}
		else if(parent.getId() == colorSpinner.getId())
		{
			String colorStr = parent.getItemAtPosition(position).toString();
			setSelectedColor(colorStr);
		}
		else if(parent.getId() == imgTypeSpinner.getId())
		{
			String imgTypeStr = parent.getItemAtPosition(position).toString();
			setSelectedImgType(imgTypeStr);
	    }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}


	@Override
	public void onClick(View v) {
		
		String selectedSite = siteTextField.getText().toString();
		 
		Intent intent = new Intent();
		String selectedImgSizeStr = getSelectedImgSize();
		String selectedColorStr = getSelectedColor();
		String selectedImgTypeStr = getSelectedImgType();
		
	    intent.putExtra("selectedImgSize", selectedImgSizeStr);
	    intent.putExtra("selectedColor", selectedColorStr);
	    intent.putExtra("selectedImgType", selectedImgTypeStr);
	    intent.putExtra("writtenSite", selectedSite);
	    setResult(RESULT_OK, intent); 
	    super.finish();
	}

	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		
        if(actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)
        {
		    selectedSite = (String) v.getText();
        }
		return true;
	}


    

}
