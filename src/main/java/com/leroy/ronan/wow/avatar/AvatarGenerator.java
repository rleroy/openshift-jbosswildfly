package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.utils.img.ImageCombinator;
import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.beans.WowCharacter;

public class AvatarGenerator {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private PersistedCache<BufferedImage> cacheSingleImages;
    private PersistedCache<BufferedImage> cacheMultiImages;
    private String cacheDir;
    private ApiClient api;
    
    public AvatarGenerator(String root, ApiClient api){
        super();
        PersistedCacheBuilder<BufferedImage> builder = new PersistedCacheBuilder<>();
        this.cacheDir = root;
        this.api = api;
        
        cacheSingleImages = builder
                .asynchro()
                .name("single")
                .loader(this::loadSingle)
                .keyToFile(key -> this.keyToFile(key, "single"))
                .fromFile(this::fromFile)
                .toFile(this::toFile)
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .build()
                ;
        cacheMultiImages = builder
                .asynchro()
                .name("multi")
                .loader(this::loadMulti)
                .keyToFile(key -> this.keyToFile(key, "multi"))
                .fromFile(this::fromFile)
                .toFile(this::toFile)
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .build()
                ;
    }
    
    public CacheResponse<BufferedImage> get(String zone, String realm, String[] characters){
        return cacheMultiImages.get(zone+"/"+realm+"/"+StringUtils.join(characters, "-"));
    }

    private BufferedImage loadSingle(String key){
        String[] keyTab = key.split("/");
        String zone = keyTab[0];
        String realm = keyTab[1];
        String name = keyTab[2];

        AvatarImage img = new AvatarImage(zone, realm, name);
        WowCharacter character = api.getCharacter(zone, realm, name);
        boolean useIlvl = true;
        if (character.getLevel() < WowCharacter.MAXLEVEL) {
        	useIlvl = false;
    	}
        BufferedImage res = null;
        try {
            res = img.getImg(useIlvl);
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }
    
    private BufferedImage loadMulti(String key) {
        String[] keyTab = key.split("/");
        String zone = keyTab[0];
        String realm = keyTab[1];
        String[] characters = keyTab[2].split("-");
        BufferedImage res = null;
        if (characters.length == 1) {
        	res = cacheSingleImages.get(key).getContent();
        }else{
	        List<CompletableFuture<CacheResponse<BufferedImage>>> futures = Stream.of(characters)
	        	.map(c -> CompletableFuture.supplyAsync(() -> cacheSingleImages.get(zone+"/"+realm+"/"+c)))
	        	.collect(Collectors.toList());
	        List<CacheResponse<BufferedImage>> responses = futures.stream()
	        	.map(f -> {
					try {
						return f.get();
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
	        	})
	        	.collect(Collectors.toList());
	        boolean notReady = responses.stream()
	        		.filter(r -> r.getContent() == null || r.isExpired())
	        		.findAny()
	        		.isPresent();
	        if (!notReady){
	        	res = responses.stream().map(r -> r.getContent()).reduce(ImageCombinator::combineLeftToRight).orElse(null);
	        }
        }
        return res;
    }

    private File keyToFile(String key, String name){
        AvatarImage img = new AvatarImage(key);
        return img.getFile(this.cacheDir, name);
    }

    private BufferedImage fromFile(File f){
        BufferedImage res = null;
        try {
            res = ImageIO.read(f);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

    private void toFile(File f, BufferedImage img){
        try {
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
