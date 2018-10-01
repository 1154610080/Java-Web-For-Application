package com.web.site;

import com.web.validation.NotBlank;

import javax.validation.constraints.Size;

/**
 * 附件POJO
 *
 * @author Egan
 * @date 2018/9/8 18:57
 **/
public class Attachment {

    @NotBlank(message = "validate.attachment.name")
    private String name;

    @NotBlank(message = "validate.attachment.mimeContentType")
    private String mimeContentType;

    @Size(min = 1, message = "validate.attachment.content")
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
