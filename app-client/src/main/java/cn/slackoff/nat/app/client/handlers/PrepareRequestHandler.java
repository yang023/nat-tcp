package cn.slackoff.nat.app.client.handlers;

import cn.slackoff.nat.app.client.components.ProxyServerPool;
import cn.slackoff.nat.app.client.components.context.ClientContext;
import cn.slackoff.nat.app.client.components.context.ClientContextHolder;
import cn.slackoff.nat.core.boot.CustomizeNettyClient;
import cn.slackoff.nat.core.data.PrepareRequest;
import cn.slackoff.nat.core.data.PrepareResponse;
import cn.slackoff.nat.core.data.TunnelInfo;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.execusion.OriginalBytesHolderHandler;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.util.Map;

/**
 * @author yang
 */
public class PrepareRequestHandler implements FrameHandler {
    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        PrepareRequest request = input.json(PrepareRequest.class);

        TunnelInfo tunnel = request.getTunnel();
        String endpoint = tunnel.getEndpoint();

        CustomizeNettyClient client = ProxyServerPool.getClient();
        ClientContext context = ClientContextHolder.getContext();
        client.setConfigurer(ch -> {
            Map<String, ChannelHandler> map = ch.pipeline().toMap();
            map.forEach((k, v) -> ch.pipeline().remove(k));

            ch.pipeline()
              .addLast(new OriginalBytesHolderHandler())
              .addLast(new HttpResponseDecoder())
              .addLast(new HttpObjectAggregator(request.getMaxRequestSize()));
        });
        context.addPreparedClient(endpoint, client);

        PrepareResponse prepareResponse = new PrepareResponse();
        prepareResponse.setChannelId(request.getChannelId());
        prepareResponse.setClientId(context.getClientId());
        prepareResponse.setEndpoint(endpoint);
        output.command(Command.PREPARE_RESPONSE)
              .contentType(ContentType.JSON)
              .write(prepareResponse);
    }

}
