package com.leroy.ronan.wow.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ApiType {
    guild("members"),
	character("items"),
	auction,
	;

    private Set<String> fields;
    
    private ApiType(String...fields){
        this.fields = new HashSet<String>(Arrays.asList(fields));
    }

    public Set<String> getFields() {
        return fields;
    }
    

}
