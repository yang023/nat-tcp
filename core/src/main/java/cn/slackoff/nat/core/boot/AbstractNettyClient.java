package cn.slackoff.nat.core.boot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author yang
 */
public abstract class AbstractNettyClient implements NettyClient {
    private static final EventLoopGroup EXECUTORS = new NioEventLoopGroup();
    private static final Class<NioSocketChannel> CHANNEL_CLASS = NioSocketChannel.class;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ChannelFutureListener onClosedListener = e -> {
    };
    private Bootstrap bootstrap;
    private int retryCount = 0;

    public void setRetryCount(final int retryCount) {
        this.retryCount = Math.max(retryCount, 0);
    }

    @Override
    public void connect(String host, int port) {
        init();

        connectInternal(new InetSocketAddress(host, port), 0);
    }

    @Override
    public void connect(String endpoint) {
        String[] split = endpoint.split(":");
        String host = split[0];
        int port = 80; // default is 80
        if (split.length > 1) {
            port = Integer.parseInt(split[1]);
        }
        connect(host, port);
    }

    @Override
    public void onClosed(ChannelFutureListener onClosedListener) {
        if (onClosedListener != null) {
            this.onClosedListener = onClosedListener;
        }
    }

    private void connectInternal(SocketAddress address, int retry) {
        this.bootstrap
                .connect(address)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("Connected to server ---> {}", address);
                    } else if (retry < retryCount) {
                        logger.info("Failed to connect to server, retry {} ---> {}", retry, address);
                        connectInternal(address, retry + 1);
                    } else if (retry == retryCount) {
                        logger.info("Failed to connect to server ---> {}", address);
                        this.onError(future);
                    }
                }).channel().closeFuture().addListener(this.onClosedListener);
    }

    private void init() {
        Bootstrap b = new Bootstrap().group(EXECUTORS)
                                     .channel(CHANNEL_CLASS);
        this.configClient(b);
        this.bootstrap = b;
    }

    protected abstract void configClient(Bootstrap bootstrap);

    protected abstract void onError(ChannelFuture channelFuture);
}
