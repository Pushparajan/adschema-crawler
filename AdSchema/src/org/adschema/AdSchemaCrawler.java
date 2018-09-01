package org.adschema;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AdSchemaCrawler {

	private static final String AD_PROPERTY = "Property";
	private static final String AD_SLOT = "Advertisement";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Document doc;
		try {
			// need http protocol
			doc = Jsoup.connect("http://www.adschema.org").get();

			JSONObject adProperty = getMetadata(doc, AdSchemaCrawler.AD_PROPERTY);

			

			//match ad slots to property
			System.out.println(adProperty);


		} catch (IOException  e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (JSONException je) {
			je.printStackTrace();
		}

	}



	private static JSONObject getAdData(Document doc, String key, String string) {
		JSONObject adJson = new JSONObject();
		for (Element div : doc.select("div")) {
			String divName = div.attr("name").toString();
			if (divName.equalsIgnoreCase(key)) {
				//System.out.println(div.toString());
				JSONObject adSlot = (JSONObject)generateJson(div);
				if(adSlot.get("adunit2productid").equals(string)) {
					//System.out.println("match");
					adJson.accumulate(key.toLowerCase(), adSlot);
				} else {
					//System.out.println("nomatch");
				}
			}
		}
		return adJson;
	}



	private static JSONObject getMetadata(Document doc, String key) {
		JSONObject adJson = new JSONObject();
		JSONObject adSlots = new JSONObject();
		for (Element div : doc.select("div")) {
			String divName = div.attr("name").toString();
			if (divName.equalsIgnoreCase(key)) {
				JSONObject generateJson = (JSONObject)generateJson(div);
				
				String adproductid = (String) generateJson.get("adproductid");
				adSlots = getAdData(doc, AdSchemaCrawler.AD_SLOT, adproductid);				
				//System.out.println(adSlots.toString());
				generateJson.put(AdSchemaCrawler.AD_SLOT.toLowerCase(), adSlots.get(AdSchemaCrawler.AD_SLOT.toLowerCase()));

				adJson = adJson.put(AdSchemaCrawler.AD_PROPERTY.toLowerCase(), generateJson);

			}
		}
		
		
		
	
		return adJson;
	}


	public static Object generateJson(Element element){
		JSONObject json = new JSONObject();
		Elements children = element.children();
		for (Element child : children) {
			if(child.tagName().equals("meta")){
				String property = child.attr("itemprop");
				json.put(property, child.attr("content"));
			} else if(json.keySet().size() == 0){
				return child.text();
			}
		}
		return json;
	}

}
