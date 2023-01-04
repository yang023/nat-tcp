package cn.slackoff.nat.app.server.runner;

import cn.slackoff.nat.app.server.handlers.HttpProxyHandler;
import cn.slackoff.nat.core.boot.AbstractNettyServer;
import cn.slackoff.nat.core.execusion.OriginalBytesHolderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.unit.DataSize;

/**
 * @author yang
 */
@Slf4j
public class HttpProxyServer extends AbstractNettyServer {
    private static final String DEFAULT_PROXY_HEADER = "X-TUNNEL-PROXY";
    private static final int DEFAULT_PROXY_REQUEST_SIZE = (int) DataSize.ofMegabytes(10).toBytes();

    @Setter
    private int maxRequestSize = DEFAULT_PROXY_REQUEST_SIZE;
    @Setter
    private String proxyHeader = DEFAULT_PROXY_HEADER;

    @Setter
    private String baseDomain;

    @Override
    protected void configServer(ServerBootstrap bootstrap) {
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OriginalBytesHolderHandler());
                pipeline.addFirst(new ByteArrayEncoder());
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new HttpObjectAggregator(maxRequestSize));
                HttpProxyHandler httpProxyHandler = new HttpProxyHandler();
                httpProxyHandler.setProxyHeader(proxyHeader);
                httpProxyHandler.setBaseDomain(baseDomain);
                httpProxyHandler.setMaxRequestSize(maxRequestSize);
                pipeline.addLast(httpProxyHandler);
            }
        });
    }

    @Override
    protected void onError(ChannelFuture channelFuture) {
        channelFuture.channel().close();
    }
}
