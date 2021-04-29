package de.terrarier.terracloud.lib;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerStartedCallbackHandler {

    private final Map<UUID, ServerStartedCallback> callbackIdMap = new ConcurrentHashMap<>();

    public UUID addCallback(ServerStartedCallback serverStartedCallback) {
        UUID generated = generateUnusedUUID();
        callbackIdMap.put(generated, serverStartedCallback);
        return generated;
    }

    public void processCallback(UUID uniqueToken, int serverId) {
        ServerStartedCallback callback = callbackIdMap.remove(uniqueToken);
        if(callback != null) {
            callback.call(serverId);
        }
    }

    private UUID generateUnusedUUID() {
        UUID ret = UUID.randomUUID();
        while(callbackIdMap.containsKey(ret)) {
            ret = UUID.randomUUID();
        }
        return ret;
    }

}
