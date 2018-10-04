package com.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * Soap派发器上下文配置类
 *  它将扫描@Endpoint组件，并导入soapServletContext.xml
 *
 * @author Egan
 * @date 2018/10/4 15:07
 **/
@Configuration
@ComponentScan(
        basePackages = "com.web.site",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(Endpoint.class)
)
@ImportResource("classpath:com/web/config/soapServletContext.xml")
public class SoapServletContextConfiguration {

    @Bean
    public WebServiceMessageFactory messageFactory(){
        SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
        factory.setSoapVersion(SoapVersion.SOAP_12);
        return factory;
    }
}
