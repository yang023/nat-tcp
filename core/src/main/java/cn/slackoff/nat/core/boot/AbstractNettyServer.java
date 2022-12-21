package cn.slackoff.nat.core.boot;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yang
 */
public abstract class AbstractNettyServer implements NettyServer {
    private static final EventLoopGroup CONNECT_GROUP = new NioEventLoopGroup();
    private static final EventLoopGroup HANDLER_GROUP = new NioEventLoopGroup();
    private static final Class<NioServerSocketChannel> CHANNEL_CLASS = NioServerSocketChannel.class;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ServerBootstrap bootstrap;

    @Override
    public void start(int port) {
        init();

        this.bootstrap.bind(port)
                      .addListener((ChannelFutureListener) channelFuture -> {
                          if (channelFuture.isSuccess()) {
                              logger.info("Server is running on {}", port);
                          } else {
                              Throwable cause = channelFuture.cause();
                              logger.error("Failed to start server: {}", cause.getMessage(), cause);
                              this.onError(channelFuture);
                          }
                      })
                      .channel()
                      .closeFuture()
                      .addListener((ChannelFutureListener) channelFuture -> {
                          logger.info("Server closed.");
                      });
    }

    private void init() {
        ServerBootstrap b = new ServerBootstrap()
                .group(CONNECT_GROUP, HANDLER_GROUP)
                .channel(CHANNEL_CLASS);
        this.configServer(b);
        this.bootstrap = b;
    }

    protected abstract void configServer(ServerBootstrap bootstrap);

    protected abstract void onError(ChannelFuture channelFuture);
}
