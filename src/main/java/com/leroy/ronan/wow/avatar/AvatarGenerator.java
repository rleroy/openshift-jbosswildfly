package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.code.joliratools.cache.ExpiringCache;
import com.leroy.ronan.utils.img.ImageCombinator;

public class AvatarGenerator {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	private Map<String, BufferedImage> cache = new ExpiringCache<>(TimeUnit.MILLISECONDS.convert(3, TimeUnit.HOURS));
	
	public BufferedImage buildImage(String region, String realm, String...characters) {
		String key = buildKey(region, realm, characters);
		BufferedImage res = cache.get(key);
		if (res == null) {
			if (characters.length > 1) {
				res = Stream.of(characters)
						.map(c -> buildImage(region, realm, c))
						.reduce(ImageCombinator::combineLeftToRight)
						.orElse(null);
			} else {
				try {
					res = getImg(region, realm, characters[0]);
				} catch (URISyntaxException | IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			cache.put(key, res);
		}
        return res;
	}
	
	private String buildKey(String region, String realm, String...characters){
		return region+","+realm+","+StringUtils.join(characters, "-");
	}

	private BufferedImage getImg(String region, String realm, String character) throws ClientProtocolException, URISyntaxException, IOException {
		log.debug("getImgUrl("+region+","+realm+","+character+")");

		BufferedImage res = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClients.custom().setDefaultCookieStore(cookieStore);
        
		try (CloseableHttpClient httpclient = builder.build()){
			this.get(cookieStore, httpclient, "http://www.best-signatures.com/api/?region="+region+"&realm="+realm+"&char="+character+"&type=Sign9&preview=1&c1=class");
			
			this.get(cookieStore, httpclient, "http://www.best-signatures.com/wow/");
	        
	        this.post(httpclient, cookieStore, 
	        		"http://www.best-signatures.com/ajax/generator/load/", 
	                new BasicNameValuePair("region", region),
	                new BasicNameValuePair("server", realm),
	                new BasicNameValuePair("char", character), // Multi : StringUtils.join(characters, ","))
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
	        JSONObject obj = (JSONObject)JSONValue.parse(json);
	        String link = StringEscapeUtils.unescapeEcmaScript((String)obj.get("link"));
	        
	        log.debug("link:"+link);
	        res = ImageIO.read(new URL(link));
		}
		return res;
	}

	private String get(BasicCookieStore cookieStore, CloseableHttpClient httpclient, String uri) throws IOException, ClientProtocolException {
		String res = null;
		HttpGet get = new HttpGet(uri);
		try (CloseableHttpResponse response = httpclient.execute(get)) {
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
		}
		return res;
	}
	
	private String post(CloseableHttpClient httpclient, BasicCookieStore cookieStore, String uri, NameValuePair...params) throws URISyntaxException, ClientProtocolException, IOException{
		String res = null;
        HttpUriRequest post = RequestBuilder.post()
        		.setUri(uri)
        		.setEntity(new UrlEncodedFormEntity(Arrays.asList(params), Charset.forName("UTF-8")))
        		.build();
        
        try (CloseableHttpResponse response = httpclient.execute(post)) {
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
        }
		return res;
	}
}
