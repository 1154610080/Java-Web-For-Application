package com.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 资源未发现异常
 *  它将返回一个404 NOT FOUND状态码
 * @author Egan
 * @date 2018/10/3 15:42
 **/
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
}
