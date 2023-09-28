package cn.nat.app.client.command.proxy;

import cn.nat.app.client.data.ProxyTunnel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author yang
 */
final class ProxyClientHandler extends ChannelInboundHandlerAdapter {
    private final ProxyTunnel tunnel;

    ProxyClientHandler(ProxyTunnel tunnel) {
        this.tunnel = tunnel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ProxyMessageSender sender = ProxyMessageSender.getOrCreate(ctx.channel());
        sender.config(tunnel);
        sender.start(null);

        ctx.channel().closeFuture().addListener(f -> sender.stop());

        sender.registerMessageListener(
                msg -> ctx.writeAndFlush(msg).addListener(
                        (ChannelFutureListener) f -> f.channel().read()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessageSender sender = ProxyMessageSender.getOrCreate(ctx.channel());

        sender.send(msg).addListener((ChannelFutureListener) f -> f.channel().read());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyMessageSender.closeSender(ctx.channel());
    }
}
