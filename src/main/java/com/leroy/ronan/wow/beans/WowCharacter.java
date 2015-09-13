package com.leroy.ronan.wow.beans;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WowCharacter extends WowJson {

    private Long lastModified;
    private String name;
    private String realm;
    private String battlegroup;
    
    private Long achievementPoints;
    
    private Long averageItemLevel;
    private Long averageItemLevelEquipped;
    
    public WowCharacter(String json) {
        super(json);
        
        JSONObject obj = (JSONObject)JSONValue.parse(json);

        lastModified = (Long)obj.get("lastModified");
        name = (String)obj.get("name");
        realm = (String)obj.get("realm");
        battlegroup = (String)obj.get("battlegroup");
        achievementPoints = (Long)obj.get("achievementPoints");
        
        JSONObject items = (JSONObject)obj.get("items");
        if (items != null){
            averageItemLevel = (Long)items.get("averageItemLevel");
            averageItemLevelEquipped = (Long)items.get("averageItemLevelEquipped");
        }
    }

    public Long getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public String getRealm() {
        return realm;
    }

    public String getBattlegroup() {
        return battlegroup;
    }

    public Long getAverageItemLevel() {
        return averageItemLevel;
    }

    public Long getAverageItemLevelEquipped() {
        return averageItemLevelEquipped;
    }

    public Long getAchievementPoints() {
        return achievementPoints;
    }

}
