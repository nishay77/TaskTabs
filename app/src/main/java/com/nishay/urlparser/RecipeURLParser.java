package com.nishay.urlparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeURLParser{
	private ArrayList<String> sites;
	public static String[] sitesArray = new String[]{"allrecipes.com", "myrecipes.com", "seriouseats.com"};

	public RecipeURLParser() {
		sites = new ArrayList<String>();
		sites.addAll(Arrays.asList(sitesArray));
	}


	/**
	 * Given a URL (for now, only AllRecipes), goes through the source and adds every item to the list (count and name) using regex
	 * @param url
	 * @return - ArrayList<String>
	 * @throws IOException 
	 */
	public ArrayList<String> parse(String url) {
		URL recipeURL;
		String site;
		try {
			recipeURL = new URL(url);
			site = recipeURL.getHost();
		} catch (Exception e) {
			//to do show error
			return null;
		}
		//check hosts
		boolean siteOK = checkSite(site);
		if(!siteOK) {
			ArrayList<String> unsupported = new ArrayList<String>();
			unsupported.add("unsupported");
			unsupported.add(site);
			return unsupported;
		}
		
		//read source
		String source = readURLSource(recipeURL);
		
		//parse out all items and add to itemlist

		return parseSource(source, site);
	}
	
	/**
	 * read html source from URL using BufferedReader and its own thread
	 * @param url
	 * @return
	 */
	private String readURLSource(final URL url) {

		final StringBuilder sb = new StringBuilder();

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader in;
				String line;
				try {
					in = new BufferedReader(new InputStreamReader(url.openStream()));

					while((line = in.readLine()) != null) {
						sb.append(line);
					}
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();

	}
	
	private ArrayList<String> parseSource(String source, String site) {
		ArrayList<String> itemList = new ArrayList<String>();
		Pattern p = Pattern.compile("");
		if(site.contains("allrecipes.com")) {
			p = Pattern.compile("itemprop=\"ingredients\">(.*?)</span>", Pattern.DOTALL);
			Matcher m = p.matcher(source);
			while(m.find()) {
				itemList.add(m.group(1));
			}
		}
		else if(site.contains("myrecipes.com")) {
			p = Pattern.compile("itemprop=\"recipeIngredient\">.*?<span>(.*?)</span>.*?<span>(.*?)</span>", Pattern.DOTALL);

			Matcher m = p.matcher(source);
			while(m.find()) {
				itemList.add(m.group(1) + m.group(2));
			}
		}
		else if(site.contains("seriouseats.com")) {
			p = Pattern.compile("itemprop=\"ingredients\">(.*?)</li>", Pattern.DOTALL);

			Matcher m = p.matcher(source);
			while(m.find()) {
				itemList.add(m.group(1));
			}
		}

		return itemList;
	}

	private boolean checkSite(String s) {
		for(String curSite : sites) {
			if(s.contains(curSite)) {
				return true;
			}
		}

		return false;
	}

}
