package project.main;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.json.JSONObject;

import com.semantics3.api.Products;

//API Key: SEM31266E9F40F0AB5D20DA00580A9E11AA6
//API Secret: ZDg5ODA0OTE4MDdlZjA4Njc4OTIzYzBiN2MyNzJkZGY

public class Semantic3Connect {
	
	public static void sendRequest(Products products) throws OAuthMessageSignerException, 
										OAuthExpectationFailedException, OAuthCommunicationException, IOException {
		products.productsField("cat_id", 4992)
				.productsField("brand", "Toshiba");
		//Make request
		JSONObject results = products.get();
		results = products.get();
		System.out.println(results);
	}
	
	public static void main(String [] args) throws OAuthMessageSignerException, 
									OAuthExpectationFailedException, OAuthCommunicationException, IOException {
		Products products = new Products(
			    "SEM31266E9F40F0AB5D20DA00580A9E11AA6",
			    "ZDg5ODA0OTE4MDdlZjA4Njc4OTIzYzBiN2MyNzJkZGY"
			);
		sendRequest(products);
	}
	
}
