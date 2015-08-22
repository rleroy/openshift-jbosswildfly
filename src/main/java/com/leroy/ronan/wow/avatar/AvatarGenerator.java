package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import com.leroy.ronan.utils.img.ImageCombinator;

public class AvatarGenerator {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	public BufferedImage buildImage(String region, String realm, String...characters) throws ClientProtocolException, IOException, URISyntaxException {
		BufferedImage res;
		if (characters.length <= 5){
            URL url = getImgUrl(region, realm, characters);
            res = ImageIO.read(url);
        } else {
        	res = ImageCombinator.combineLeftToRight(
    				buildImage(region, realm, Arrays.copyOfRange(characters, 0, 4)),
    				buildImage(region, realm, Arrays.copyOfRange(characters, 4, characters.length))
    			);
        }
        return res;
	}

	private URL getImgUrl(String region, String realm, String... characters)
			throws IOException, ClientProtocolException, URISyntaxException, MalformedURLException {
		URL url;
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
		try {
	        get(cookieStore, httpclient, "http://www.best-signatures.com/wow/");
	        
	        this.post(httpclient, cookieStore, 
	        		"http://www.best-signatures.com/ajax/generator/load/", 
	                new BasicNameValuePair("region", region),
	                new BasicNameValuePair("server", realm),
	                new BasicNameValuePair("char", StringUtils.join(characters, ",")),
	                new BasicNameValuePair("lang", "en_GB")
	        		);
	        this.post(httpclient, cookieStore, 
	        		"http://www.best-signatures.com/ajax/generator/settype/", 
	        		new BasicNameValuePair("signType", "Sign22")
	        		);
	        this.post(httpclient, cookieStore, 
	        		"http://www.best-signatures.com/ajax/generator/setstyle/", 
	        		new BasicNameValuePair("style[avatar][type]", "portrait"),
	        		new BasicNameValuePair("style[color][1]", "#FFFFFF"),
	        		new BasicNameValuePair("style[effect]", "noeffect"),
	        		new BasicNameValuePair("style[other][name]", "1"),
	        		new BasicNameValuePair("style[other][classcolor]", "1"),
	        		new BasicNameValuePair("style[other][race]", "0"),
	        		new BasicNameValuePair("style[other][ilvl]", "1")
	        		);
	        String json = this.post(httpclient, cookieStore, 
	        		"http://www.best-signatures.com/ajax/generator/save/", 
	        		new BasicNameValuePair("save", "1")
	        		);
		
	        String link = StringEscapeUtils.unescapeEcmaScript(getJSonValue(json, "link"));
	        log.debug("link:"+link);
	        url = new URL(link);
		} finally {
	        httpclient.close();
	    }
		return url;
	}

	private String get(BasicCookieStore cookieStore, CloseableHttpClient httpclient, String uri) throws IOException, ClientProtocolException {
		String res = null;
		HttpGet get = new HttpGet(uri);
		CloseableHttpResponse response1 = httpclient.execute(get);
		try {
		    HttpEntity entity = response1.getEntity();

		    res = EntityUtils.toString(entity);
		    log.debug("Content:"+res);
		    EntityUtils.consume(entity);

		    log.debug("Cookies:");
		    List<Cookie> cookies = cookieStore.getCookies();
		    if (cookies.isEmpty()) {
		        log.debug("None");
		    } else {
		        for (int i = 0; i < cookies.size(); i++) {
		            log.debug("- " + cookies.get(i).toString());
		        }
		    }
		} finally {
		    response1.close();
		}
		return res;
	}
	
	private String post(CloseableHttpClient httpclient, BasicCookieStore cookieStore, String uri, NameValuePair...params) throws URISyntaxException, ClientProtocolException, IOException{
		String res = null;
        HttpUriRequest post = RequestBuilder.post()
                .setUri(new URI(uri))
                .addParameters(params)
                .build();
        CloseableHttpResponse response = httpclient.execute(post);
        try {
            HttpEntity entity = response.getEntity();

		    res = EntityUtils.toString(entity);
		    EntityUtils.consume(entity);

            log.debug("Cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                log.debug("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    log.debug("- " + cookies.get(i).toString());
                }
            }
        } finally {
            response.close();
        }
		return res;
	}

	private String getJSonValue(String json, String value) {
		JsonParserFactory factory = JsonParserFactory.getInstance();
		JSONParser parser = factory.newJsonParser();
		Map jsonData = parser.parseJson(json);

		String link = (String)jsonData.get(value);
		return link;
	}

}
