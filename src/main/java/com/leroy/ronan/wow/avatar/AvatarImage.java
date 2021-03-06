package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AvatarImage {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private String zone;
    private String realm;
    private String character;

    public AvatarImage(String key){
        String[] keyTab = key.split("/");
        this.zone = keyTab[0];
        this.realm = keyTab[1];
        this.character = keyTab[2];
    }

    public AvatarImage(String zone, String realm, String character) {
        this.zone = zone;
        this.realm = realm;
        this.character = character;
    }

    public String getZone() {
        return zone;
    }

    public String getRealm() {
        return realm;
    }

    public String getCharacter() {
        return character;
    }
    
    public String getKey(){
        return zone+"/"+realm+"/"+character;
    }
    
    public File getFile(String rootDir, String name) {
        return Paths.get(rootDir+"/"+zone+"/"+"img-"+name+"/"+realm+"/"+character+".png").toFile();
    }

    public BufferedImage getImg(boolean useIlvl) throws ClientProtocolException, URISyntaxException, IOException {
        log.debug("getImg("+zone+","+realm+","+character+")");

        BufferedImage res = null;
        BasicCookieStore cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClients.custom().setDefaultCookieStore(cookieStore);

        try (CloseableHttpClient httpclient = builder.build()){
            this.get(cookieStore, httpclient, "http://www.best-signatures.com/api/?region="+zone+"&realm="+realm+"&char="+character+"&type=Sign9&preview=1&c1=class");

            this.get(cookieStore, httpclient, "http://www.best-signatures.com/wow/");

            this.post(httpclient, cookieStore, 
                    "http://www.best-signatures.com/ajax/generator/load/", 
                    new BasicNameValuePair("region", zone),
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
                    new BasicNameValuePair("style[other][ilvl]", useIlvl?"1":"0")
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

        log.debug("http get : "+uri);

        HttpGet get = new HttpGet(uri);
        try (CloseableHttpResponse response = httpclient.execute(get)) {
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }
        return res;
    }

    private String post(CloseableHttpClient httpclient, BasicCookieStore cookieStore, String uri, NameValuePair...params) throws URISyntaxException, ClientProtocolException, IOException{
        String res = null;

        log.debug("http post : "+uri);

        HttpUriRequest post = RequestBuilder.post()
                .setUri(uri)
                .setEntity(new UrlEncodedFormEntity(Arrays.asList(params), Charset.forName("UTF-8")))
                .build();

        try (CloseableHttpResponse response = httpclient.execute(post)) {
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        }
        return res;
    }

}
