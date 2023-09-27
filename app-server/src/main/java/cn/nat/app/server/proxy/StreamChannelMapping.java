package cn.nat.app.server.proxy;

import cn.nat.app.server.utils.ChannelMapping;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author yang
 */
public final class StreamChannelMapping extends ChannelMapping {
    private static final AttributeKey<String> BINDING_STREAM_KEY = AttributeKey.valueOf("binding-key");
    private static final AttributeKey<Consumer<Object>> STREAM_MESSAGING_KEY = AttributeKey.valueOf("stream-messaging");

    public static void add(String tunnel, String bindKey, Channel channel) {
        getInstance().addChannel(tunnel, channel);

        channel.attr(BINDING_STREAM_KEY).set(bindKey);
    }

    public static Channel streamWithBindKey(String tunnel, String bindKey, Duration timeout) {
        AtomicReference<Channel> reference = new AtomicReference<>();
        try {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    Channel ch = getInstance().selectChannel(tunnel, channels -> {
                        for (Channel channel : channels) {
                            Attribute<String> attr = channel.attr(BINDING_STREAM_KEY);
                            String bindKeyPreset = attr.get();
                            if (bindKeyPreset == null) {
                                continue;
                            }

                            if (bindKeyPreset.equals(bindKey)) {
                                return channel;
                            }
                        }
                        return null;
                    });

                    if (ch != null) {
                        reference.set(ch);
                        break;
                    }
                }
            }).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            //
        }

        return reference.get();
    }

    public static void registerMessageListener(Channel stream, Consumer<Object> listener) {
        stream.attr(STREAM_MESSAGING_KEY).set(listener);
    }

    public static void triggerMessage(Channel stream, Object msg) {
        Consumer<Object> consumer = stream.attr(STREAM_MESSAGING_KEY).get();
        if (consumer != null) {
            consumer.accept(msg);
        }
    }

    private StreamChannelMapping() {
    }

    private static class InstanceHolder {
        private static final StreamChannelMapping INSTANCE = new StreamChannelMapping();
    }

    private static StreamChannelMapping getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
