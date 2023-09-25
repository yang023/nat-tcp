package cn.nat.app.client.command;

import cn.nat.app.client.config.ClientConfig;
import cn.nat.common.data.ClientStartupFrame;
import cn.nat.common.protocol.Frame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yang
 */
final class Startup {
    private ClientConfig config;

    Startup config(ClientConfig config) {
        this.config = config;
        return this;
    }

    void execute(ChannelFuture future) {
        List<String> tunnels = config.getTunnels().stream().map(ClientConfig.Tunnel::getName).toList();
        Frame frame = new ClientStartupFrame().clientId(config.getClientId()).tunnels(tunnels).createFrame();
        ClientConfig.Server server = config.getServer();
        ByteBuf data = frame.serialize();
        InetSocketAddress targetAddr = new InetSocketAddress(server.getHost(), server.getPort());

        DatagramPacket packet = new DatagramPacket(data, targetAddr);

        Channel channel = future.channel();
        channel.writeAndFlush(packet).addListener(f -> {
            System.out.println(f.isSuccess());
        });
    }

}
