package de.terrarier.lib;

import de.terrarier.netlistening.Application;
import de.terrarier.netlistening.Connection;
import de.terrarier.netlistening.api.DataContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CustomPayloadUtil {

    private static Application app;
    public static final ServerStartedCallbackHandler SERVER_STARTED_CALLBACK_HANDLER = new ServerStartedCallbackHandler();

    private CustomPayloadUtil() {}

    public static void init(Application application) {
        app = application;
    }

    public static void prepareServer(String serverName) {
        final String[] split = serverName.split("-");

        if(split.length < 2 || !TypeCheckUtil.isInteger(split[1]))
            return;

        getBuilder().internal().payloadId(-1).targetGroup(split[0]).targetServerId(Integer.parseInt(split[1])).write();
    }

    public static void triggerCallback(Connection target, UUID identifier, int serverId) {
        getBuilder().internal().payloadId(-8).data(identifier, serverId).write(target);
    }

    public static void sendGlobalMessage(String message, String requiredPermission) {
        sendMessage(message, null, requiredPermission);
    }

    public static void sendGlobalMessage(String message) {
        sendMessage(message, null, null);
    }

    public static void sendMessage(String message, String receiver) {
        sendMessage(message, receiver, null);
    }

    public static void sendMessage(String message, String receiver, String requiredPermission) {
        PayloadBuilder builder = getBuilder().internal().payloadId(-2);
        byte setting = 0;
        if(receiver != null)
            setting = BitUtil.modifyBit(setting, 1, 1);

        if(requiredPermission != null)
            setting = BitUtil.modifyBit(setting, 2, 1);

        List<Object> data = new ArrayList<>();

        data.add(setting);
        data.add(message);
        if(receiver != null)
            data.add(receiver);
        if(requiredPermission != null)
            data.add(requiredPermission);
        builder.data(data.toArray());
        builder.write();
    }

    public static void startOrCreateServer(String groupName, int id, ServerVersion serverVersion) {
        getBuilder().internal().payloadId(-3).targetGroup(groupName).targetServerId(id).data(serverVersion.name()).write();
    }

    public static void startOrCreateServer(String groupName, int id) {
        getBuilder().internal().payloadId(-3).targetGroup(groupName).targetServerId(id).write();
    }

    public static void startOrCreateServer(String groupName, ServerStartedCallback serverStartedCallback) { // TODO: Test callback function
        getBuilder().internal().payloadId(-3).targetGroup(groupName)
                .data(SERVER_STARTED_CALLBACK_HANDLER.addCallback(serverStartedCallback)).write();
    }

    public static void startOrCreateServer(String groupName) {
        getBuilder().internal().payloadId(-3).targetGroup(groupName).write();
    }

    public static void stopServer(String groupName, int id) {
        getBuilder().internal().payloadId(-4).targetGroup(groupName).targetServerId(id).write();
    }

    public static void startProxy(String groupName) {
        getBuilder().internal().payloadId(-5).targetGroup(groupName).write();
    }

    public static void startProxy(String groupName, int id) {
        getBuilder().internal().payloadId(-5).targetGroup(groupName).targetServerId(id).write();
    }

    public static void stopProxy(String groupName, int id) {
        getBuilder().internal().payloadId(-6).targetGroup(groupName).targetServerId(id).write();
    }

    public static void updatePermissions(PermissionAction action, String value, String groupName) {
        getBuilder().internal().payloadId(-7).data(action.ordinal(), value, groupName).write();
    }

    public static void updatePermissions(PermissionAction action, String value, UUID uuid) {
        getBuilder().internal().payloadId(-7).data(action.ordinal(), value, uuid).write();
    }

    public static PayloadBuilder getBuilder() {
        return new PayloadBuilder();
    }

    private static class PayLoadSetting {

        private final boolean[] values;

        public PayLoadSetting() {
            this.values = new boolean[4];
        }

        public PayLoadSetting(boolean[] values) {
            this.values = values;
        }

        public void setValue(int index, boolean value) {
            this.values[index] = value;
        }

        public boolean get(int index) {
            return this.values[index];
        }

        public byte toByte() {
            return DataCompressionUtil.toByte(this.values);
        }

        private static PayLoadSetting fromByte(byte value) {
            return new PayLoadSetting(DataCompressionUtil.fromByte(value, 4));
        }

    }

    public static class PayloadBuilder {

        private PayLoadSetting setting = new PayLoadSetting();
        private String group;
        private int serverId = -1;
        private int id = -99;
        private String channel;
        private Object[] data;
        private boolean internal;

        public PayloadBuilder() {}

        public PayloadBuilder(int id) {
            this.id = id;
        }

        public PayloadBuilder targetGroup(String targetGroup) {
            this.group = targetGroup;
            this.setting.setValue(0, true);
            return this;
        }

        public PayloadBuilder targetServerId(int id) {
            this.serverId = id;
            this.setting.setValue(2, true);
            return this;
        }

        protected PayloadBuilder internal() {
            this.internal = true;
            return this;
        }

        public PayloadBuilder targetGroupIsBungee(boolean isBungee) {
            this.setting.setValue(1, isBungee);
            return this;
        }

        public PayloadBuilder payloadId(int id) {
            if(!this.internal) {
               // id = Math.max(0, id);
                id = id * -1;
            }
            this.id = id;
            return this;
        }

        public PayloadBuilder channel(String channel) {
            setting.setValue(3, channel != null);
            this.channel = channel;
            return this;
        }

        public PayloadBuilder data(Object... data) {
            if(data != null && data.length != 0)
                this.data = data;

            return this;
        }

        public boolean doHandleInternally() {
            return this.id < 0;
        }

        public boolean doTargetSpecificGroup() {
            return this.setting.get(0);
        }

        public boolean doTargetSpecificServer() {
            return this.setting.get(1);
        }

        public int getId() {
            return this.id;
        }

        public boolean isIdAvailable() {
            return this.id != 0;
        }

        public String getGroup() {
            return this.group;
        }

        public int getServerId() {
            return this.serverId;
        }

        public String getChannel() {
            return this.channel;
        }

        public boolean isChannelAvailable() {
            return this.channel != null;
        }

        public Object[] getData() {
            return this.data;
        }

        public boolean isProxyGroup() {
            return this.setting.get(2);
        }

        public void write() {
            write(null);
        }

        public void write(Connection connection) {
            if(this.id == -99 && this.channel != null) {
                this.id = 0;
            }
            if(this.id != -99 && this.serverId > -2) {
                DataContainer container = new DataContainer();

                container.add(0x7);
                container.add(this.id);
                container.add(this.setting.toByte());

                if(this.channel != null)
                    container.add(this.channel);

                if(this.group != null) {
                    container.add(this.group);
                    if(this.serverId > -1)
                        container.add(this.serverId);
                }

                if(this.data != null)
                    container.addAll(this.data);

                if(connection != null) {
                    connection.sendData(container);
                }else {
                    app.sendData(container);
                }
            }
        }

        public void read(DataContainer container) {
            if(this.id == -99)
            this.id = container.read(); // this line causes errors (cast exceptions from String to Integer)
            
            this.setting = PayLoadSetting.fromByte(container.read());

            if (this.id > -2) {
                if(this.setting.get(3)) {
                    this.channel = container.read();
                }
                if (this.setting.get(0)) {
                    this.group = container.read();

                    if (this.setting.get(2))
                        this.serverId = container.read();
                }
            }

            this.data = container.isReadable() ? container.readRemaining() : null;
        }
    }

}

