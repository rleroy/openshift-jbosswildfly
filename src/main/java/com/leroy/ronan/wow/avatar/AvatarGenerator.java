package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.utils.img.ImageCombinator;

public class AvatarGenerator {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private PersistedCache<BufferedImage> cache;
    private String cacheDir;
    
    public AvatarGenerator(String cacheDir){
        super();
        PersistedCacheBuilder<BufferedImage> builder = new PersistedCacheBuilder<>();
        this.cacheDir = cacheDir;
        cache = builder
                .loader(this::load)
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .keyToFile(this::keyToFile)
                .fromFile(this::fromFile)
                .toFile(this::toFile)
                .build()
                ;
    }
    
    public CacheResponse<BufferedImage> get(String region, String realm, String[] characters){
        return cache.get(region+"/"+realm+"/"+StringUtils.join(characters, "-"));
    }

    private BufferedImage load(String key){
        String[] keyTab = key.split("/");
        String region = keyTab[0];
        String realm = keyTab[1];
        String[] characters = keyTab[2].split("-");
        
        BufferedImage res = null;
        if (characters.length > 1){
            res = Stream.of(characters)
                    .map(c -> cache.get(region+"/"+realm+"/"+c))
                    .map(r -> r.getContent())
                    .reduce(ImageCombinator::combineLeftToRight)
                    .orElse(null);
        } else {
            AvatarImage img = new AvatarImage(region, realm, characters[0]);
            try {
                res = img.getImg();
            } catch (URISyntaxException | IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return res;
    }

    private File keyToFile(String key){
        AvatarImage img = new AvatarImage(key);
        return img.getFile(this.cacheDir);
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
