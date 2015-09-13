package com.leroy.ronan.wow.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.api.cache.ApiClientCached;
import com.leroy.ronan.wow.avatar.AvatarGenerator;

public class ServiceProvider {

	public static ApiClient getApi(String root){
		return (ApiClient)getService("api", () -> new ApiClientCached("en_GB", "8vkxyhwqkb787e47utga6r5djuw2unqt", root));
	}
	
	public static AvatarGenerator getAvatar(String root){
		return (AvatarGenerator)getService("avatar", () -> new AvatarGenerator(root, getApi(root)));
	}
	
	private static final Map<String, Object> services = new HashMap<>();
	private static Object getService(String name, Supplier<Object> supplier) {
		if (!services.containsKey(name)){
			synchronized (name.intern()) {
				if (!services.containsKey(name)){
					Object service = supplier.get();
					services.put(name, service);
				}
			}
		}
		return services.get(name);
	}
	
}
