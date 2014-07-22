package project.checkout.main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

/**
 * Simple demo application illustrating the use of the Scandit SDK.
 * 
 * Important information for the developer with respect to Android 2.1 support!
 * 
 * Android 2.1 differs from subsequent versions of Android OS in that it 
 * does not offer a camera preview mode in portrait mode (landscape only). 
 * Android 2.2+ offers both - a camera preview in landscape mode and in portrait 
 * mode. There are certain devices that run Android 2.2+ but do not properly
 * implement the methods needed for a portrait camera view. 
 * 
 * To address this difference between the Android versions, the Scandit SDK 
 * offers the following approaches and the developer needs to choose his 
 * preferred option:
 * 
 * If you are showing the scanner on the full screen in a new Activity:
 * 
 * - Instantiate the ScanditSDKAutoAdjustingBarcodePicker which will choose 
 * whether to use the new or legacy picker.
 * 
 * If you want to show the picker inside a view hierarchy/cropped/scaled you
 * have to make the distinction between the different pickers yourself. Fore 
 * devices that don't support the new picker the following options exist:
 * 
 * - a scan view in landscape mode scanning only(!) that is fully 
 * customizable by the developer - ScanditSDKBarcodePicker.class
 * 
 * - our own custom scan view with portrait mode scanning that offers only 
 * limited customization options (show/hide title & tool bars, 
 * but no additional Android UI elements) -  LegacyPortraitScanditSDKBarcodePicker.class
 * 
 * For devices that do support the new picker the following options exist:
 * 
 * - a scan view with portrait mode scanning that is fully customizable 
 * by the developer (RECOMMENDED) - ScanditSDKBarcodePicker.class
 * 
 * - any of the options listed under Android 2.1
 * 
 * We recommend that developers choose the scan view in portrait mode on Android 2.2.
 * It has the native Android look&feel and provides full customization. We provide our
 * own custom scan view (LegacyPortraitScanditSDKBarcodePicker.class) in Android 2.1
 * to provide backwards compatibility with Android 2.1. 
 *
 * To integrate the Scandit SDK, carry out the following three steps:
 * 
 * 1. Create a BarcodePicker object that manages camera access and 
 *    bar code scanning:
 *    
 *    e.g.
 *    ScanditSDKBarcodePicker barcodePicker = new ScanditSDKBarcodePicker(this, 
 *              R.raw.class, "your app key", true, 
                ScanditSDKBarcodePicker.LOCATION_PROVIDED_BY_SCANDIT_SDK);
 *
 *  IMPORTANT: Make sure add your app key here. It is available from your Scandit SDK account. 
 *
 * 2. Add it to the activity:    
 *    my_activity.setContentView(barcodePicker);
 * 
 * 3. Implement the ScanditSDKListener interface (didCancel, didScanBarcode, 
 *    didManualSearch) and register with the ScanditSDKOverlayView to receive 
 *    callbacks:
 *    barcodePicker.getOverlayView().addListener(this);
 * 
 * 
 * If you want to use the custom scan view for scanning in portrait mode in 
 * Android 2.1, instantiate the LegacyPortraitScanditSDKBarcodePicker
 * class (as shown in the example below). There is utility method available 
 * to determine whenever the default portrait scan view is not available
 * ScanditSDKBarcodePicker.canRunPortraitPicker().
 * 
 * 
 * 
 * Copyright 2010 Mirasense AG
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing premissions and
 * limitations under the License.
 */
public class PayAsYouGo extends Activity implements ScanditSDKListener {

    // The main object for recognizing a displaying barcodes.
	private String scanditApiLink = "https://api.scandit.com/v2/products/";
    private ScanditSDK mBarcodePicker;
    private String walmartApiLinkFirstHalf = "http://api.walmartlabs.com/v1/search?apiKey=";
    private String walmartApiLinkSecondHalf = "&query=";
    private String toSet = "";
    private ArrayList<CartItem> cart = new ArrayList<CartItem>();
    
    //overall link that will return the JSON object is apiLink+barcode+?+key=+cleanedBarcode+"?"+"key="sScanditSdkAppKey
    //example: https://api.scandit.com/v2/products/9781401323257?key=a390vup2xkl6nDeJ3mXI7jT
    //this will return a JSON object.  For now, display the info from the JSON on the screen as a Toast
    
    //To run query with Walmart API, use http://api.walmartlabs.com/v1/search?apiKey={apiKey}&query={UPC}
    
    // Enter your Scandit SDK App key here.
    // Your Scandit SDK App key is avail able via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "ssXCBgyBEeSC49LT/JBa3QJKCErH3i9NgTH7beCm8ps";
    private static final String walmartKey = "s63p25pp2swxvtthpn8p6a9j";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }
    
    @Override
    protected void onPause() {
        // When the activity is in the background immediately stop the 
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();

        super.onPause();
    }
    
    @Override
    protected void onResume() {
        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();
        super.onResume();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // We instantiate the automatically adjusting barcode picker that will
        // choose the correct picker to instantiate. Be aware that this picker
        // should only be instantiated if the picker is shown full screen as the
        // legacy picker will rotate the orientation and not properly work in
        // non-fullscreen.
        ScanditSDKAutoAdjustingBarcodePicker picker = new ScanditSDKAutoAdjustingBarcodePicker(
                    this, sScanditSdkAppKey, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);
        
        // Add both views to activity, with the scan GUI on top.
        setContentView(picker);
        mBarcodePicker = picker;
        
        // Register listener, in order to be notified about relevant events 
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.getOverlayView().addListener(this);
        
        // Show a search bar in the scan user interface.
        mBarcodePicker.getOverlayView().showSearchBar(true);
    }

    /** 
     *  Called when a barcode has been decoded successfully.
     *  
     *  @param barcode Scanned barcode content.
     *  @param symbology Scanned barcode symbology.
     */
    public void didScanBarcode(String barcode, String symbology) {
        // Remove non-relevant characters that might be displayed as rectangles
        // on some devices. Be aware that you normally do not need to do this.
        // Only special GS1 code formats contain such characters.
        String cleanedBarcode = "";
        for (int i = 0 ; i < barcode.length(); i++) {
            if (barcode.charAt(i) > 30) {
                cleanedBarcode += barcode.charAt(i);
            }
        }
        	
        HttpGetter get = new HttpGetter();
        try {
            get.execute(new URI(walmartApiLinkFirstHalf+walmartKey+walmartApiLinkSecondHalf+cleanedBarcode));
			Toast.makeText(this, toSet, Toast.LENGTH_LONG).show();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }	
        	//JSONObject json = new JSONObject(PayAsYouGo.convertStreamToString(response.getEntity().getContent()));
		@Override
		public void didCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
	    public void didManualSearch(String entry) {
		    HttpGetter get = new HttpGetter();
		    try
		    {
    		    get.execute(new URI(walmartApiLinkFirstHalf+walmartKey+walmartApiLinkSecondHalf+entry));
    	    	Toast.makeText(this, "User entered: " + toSet, Toast.LENGTH_LONG).show();
		    } catch (URISyntaxException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }

public static String convertStreamToString(InputStream inputStream) throws IOException {
    if (inputStream != null) {
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            inputStream.close();
        }
        return writer.toString();
    } else {
        return "";
    }
}

	private class HttpGetter extends AsyncTask<URI, Void, Void> {
		@Override
		protected Void doInBackground(URI... params) {

			try {
				HttpResponse response = null;
				
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(params[0]);
				response = client.execute(get);
				if(response==null) {
			    	toSet = "Failed";
			    	return null;
				}
				toSet = PayAsYouGo.convertStreamToString(response.getEntity().getContent());

			}catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}