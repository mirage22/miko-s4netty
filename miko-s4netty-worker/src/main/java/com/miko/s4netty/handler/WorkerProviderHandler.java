package com.miko.s4netty.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.miko.s4netty.dto.CarDTO;
import com.miko.s4netty.service.SimpleCarService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/*
 * The MIT License
 *
 * Copyright 2014 Miroslav Kopecky.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * @author miroslavkopecky
 *
 * Handling web request and providign JSON response
 * Data are taken from the service and coverted to JSONArray by GSon
 */

@Component
@ChannelHandler.Sharable
@Qualifier("workerProviderHandler")
public class WorkerProviderHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WorkerProviderHandler.class);

    @Autowired
    SimpleCarService simpleCarService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            boolean keepAlive = isKeepAlive(req);

            logger.debug("channelRead keepAlive= " + keepAlive);

            Gson gson = new Gson();
            List<CarDTO> carList = simpleCarService.getAll();

            Map<String, List<CarDTO>> byMake = simpleCarService.getAll().stream().collect(Collectors.groupingBy(CarDTO::getMake));
            System.out.println("byMake = " + byMake);
            System.out.println("byMake Skoda= " + byMake.get("Skoda").iterator().next().getModel());



            JsonElement element = gson.toJsonTree(carList, new TypeToken<List<CarDTO>>() {}.getType());
            JsonArray jsonArray = element.getAsJsonArray();

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonArray.toString().getBytes()));
            response.headers().set(CONTENT_TYPE, "application/json");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            //ignoring KeepAlive
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.debug("channelReadComplete ctx= " + ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exception= " + cause);
        ctx.close();
    }

}
