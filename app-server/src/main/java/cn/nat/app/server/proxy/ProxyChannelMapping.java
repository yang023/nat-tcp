package cn.nat.app.server.proxy;


import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.util.function.Consumer;

/**
 * @author yang
 */
final class ProxyChannelMapping {
    private static final AttributeKey<Channel> GATEWAY_CHANNEL_KEY = AttributeKey.valueOf("gateway-channel");
    private static final AttributeKey<Consumer<Object>> MESSAGE_EVENT_KEY = AttributeKey.valueOf("stream-event");

    static void bind(Channel gateway, Channel stream, Consumer<Object> onUnbind) {
        Attribute<Channel> gatewayAttr = stream.attr(GATEWAY_CHANNEL_KEY);
        gatewayAttr.setIfAbsent(gateway);

        Attribute<Consumer<Object>> eventAttr = stream.attr(MESSAGE_EVENT_KEY);
        eventAttr.setIfAbsent(onUnbind);

        gateway.closeFuture().addListener(f -> unbind(stream));

    }

    static boolean isBound(Channel stream) {
        Attribute<Channel> channelAttr = stream.attr(GATEWAY_CHANNEL_KEY);
        return channelAttr.get() != null;
    }

    static void triggerStream(Channel stream, Object msg) {
        Attribute<Consumer<Object>> eventAttr = stream.attr(MESSAGE_EVENT_KEY);
        Consumer<Object> consumer = eventAttr.get();
        if (consumer != null) {
            consumer.accept(msg);
        } else {
            // gateway已被关闭，丢弃消息
            ReferenceCountUtil.release(msg);
        }
    }

    static void unbind(Channel stream) {
        Attribute<Channel> channelAttr = stream.attr(GATEWAY_CHANNEL_KEY);
        Attribute<Consumer<Object>> eventAttr = stream.attr(MESSAGE_EVENT_KEY);
        eventAttr.set(null);

        Channel gateway = channelAttr.get();
        if (gateway == null) {
            return;
        }
        channelAttr.set(null);
    }
}