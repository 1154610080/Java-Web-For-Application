package com.web.site;

import org.springframework.web.servlet.View;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义下载视图
 *
 * 本视图用于将文件附加到响应中返回到客户端，以实现文件下载
 *
 * @author Egan
 * @date 2018/9/8 19:46
 **/
public class DownloadView implements View {

    private final String fileName;

    private final String contentType;

    private final byte[] contents;

    public DownloadView(String fileName, String contentType, byte[] contents) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.contents = contents;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void render(Map<String, ?> map,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws Exception {

        httpServletResponse.setHeader("Content-Disposition",
                    "attachment; fileName="+fileName);
        httpServletResponse.setContentType("application/octet-stream");

        ServletOutputStream stream = httpServletResponse.getOutputStream();
        stream.write(contents);
    }
}
