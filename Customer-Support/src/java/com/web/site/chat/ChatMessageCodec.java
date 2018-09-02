package com.web.site.chat;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 聊天消息编码类
 *
 *   本类将使用jackson数据处理器编码和解码消息。
 *
 *   编码方法将接受一个ChatMessage和一个OutputStream，
 * 通过将它转换成JSON对消息进行编码，并将它写入OutputStream中
 *
 *   解码方法将根据所提供的的InputStream读取并反序列化JSON ChatMessage
 *
 * @author Egan
 * @date 2018/9/2 15:49
 **/
public class ChatMessageCodec
    implements Encoder.BinaryStream<ChatMessage>,
        Decoder.BinaryStream<ChatMessage>{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
        MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }

    @Override
    public ChatMessage decode(InputStream inputStream) throws DecodeException, IOException {
        try{
            return ChatMessageCodec.MAPPER.readValue(inputStream, ChatMessage.class);
        }catch (JsonParseException | JsonMappingException e){
            throw new DecodeException((ByteBuffer) null, e.getMessage(), e);
        }
    }

    @Override
    public void encode(ChatMessage chatMessage, OutputStream outputStream) throws EncodeException, IOException {
        try {
            ChatMessageCodec.MAPPER.writeValue(outputStream, chatMessage);
        }catch (JsonGenerationException | JsonMappingException e){
            throw new EncodeException(chatMessage, e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }
}
