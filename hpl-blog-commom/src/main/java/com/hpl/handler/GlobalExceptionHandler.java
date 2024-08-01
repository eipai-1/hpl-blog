package com.hpl.handler;

/**
 * @author : rbe
 * @date : 2024/7/26 10:48
 */

import com.hpl.exception.CommonException;
import com.hpl.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public CommonResult exceptionHandler(CommonException ex){
        log.error("异常信息：{}", ex.getStatus().getMsg());
        return CommonResult.error(ex.getStatus().getCode(),ex.getStatus().getMsg());
    }

//    /**
//     * 处理SQL异常
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler
//    public CommonResult exceptionHandler(SQLIntegrityConstraintViolationException ex){
//        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
//        String message = ex.getMessage();
//        if(message.contains("Duplicate entry")){
//            String[] split = message.split(" ");
//            String username = split[2];
//            String msg = username + MessageConstant.ALREADY_EXISTS;
//            return CommonResult.error(msg);
//        }else{
//            return CommonResult.error(MessageConstant.UNKNOWN_ERROR);
//        }
//    }
}

