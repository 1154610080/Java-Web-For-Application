package com.web.site;

/**
 * 附件POJO
 *
 * @author Egan
 * @date 2018/9/8 18:57
 **/
public class Attachment {

    private String name;
    private String mimeContentType;
    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeContentType() {
        return mimeContentType;
    }

    public void setMimeContentType(String mimeContentType) {
        this.mimeContentType = mimeContentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
