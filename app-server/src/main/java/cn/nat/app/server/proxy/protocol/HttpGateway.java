package cn.nat.app.server.proxy.protocol;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.proxy.AbstractGatewayServer;
import cn.nat.app.server.proxy.ProxyMessageHandler;
import cn.nat.common.data.ErrorFrame;
import cn.nat.common.utils.BufferUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author yang
 */
@Component
public class HttpGateway extends AbstractGatewayServer {
    private static final String DEFAULT_TUNNEL_KEY = "x-tunnel";
    private static final String DEFAULT_TUNNEL_PARAMETER = "tunnel";

    private String tunnelKey;

    @Autowired
    public void setConfig(ServerConfig config) {
        config(config.getGateway("http"));
    }

    @Override
    public AbstractGatewayServer config(ServerConfig.Gateway config) {
        Map<String, String> properties = config.getProperties();
        tunnelKey = properties.getOrDefault(DEFAULT_TUNNEL_PARAMETER, DEFAULT_TUNNEL_KEY);
        return super.config(config);
    }

    @Override
    protected void applyGatewayCodec(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
    }

    @Override
    protected ProxyMessageHandler createMessageHandler() {
        return new HttpMessageHandler(tunnelKey);
    }

    @Override
    protected int resolvePort(ServerConfig.Gateway gateway) {
        return gateway.getPort(8080);
    }

    @Override
    protected String print(ServerConfig.Gateway config) {
        return "Http Gateway Server start on :%d".formatted(config.getPort());
    }

    static class HttpMessageHandler implements ProxyMessageHandler {
        private final String tunnelKey;

        HttpMessageHandler(String tunnelKey) {
            this.tunnelKey = tunnelKey;
        }

        @Override
        public boolean isFirstRequest(Object msg) {
            return msg instanceof HttpRequest;
        }

        @Override
        public boolean isLastResponse(Object msg) {
            return msg instanceof LastHttpContent;
        }

        @Override
        public boolean closeOnLastResponse() {
            return true;
        }

        @Override
        public void applyStreamCodec(ChannelPipeline pipeline) {
            pipeline.addFirst(new HttpResponseDecoder());
            pipeline.addFirst(new HttpRequestEncoder());
        }

        @Override
        public String findTunnelKey(Object msg) throws IOException {
            HttpRequest request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();
            String tunnel = headers.get(DEFAULT_TUNNEL_KEY);
            headers.remove(tunnelKey);
            return tunnel;
        }

        @Override
        public void sendError(Channel gateway, ErrorFrame error) throws IOException {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(error.code()));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf8");
            BufferUtil.writeLine(response.content(), error.message(), "");
            gateway.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
