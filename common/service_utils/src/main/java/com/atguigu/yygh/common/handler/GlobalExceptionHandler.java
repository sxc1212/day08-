package com.atguigu.yygh.common.handler;


import com.atguigu.yygh.common.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
    /**
     * 统一异常处理类
     */
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        @ResponseBody
        public R error(Exception e){
            e.printStackTrace();
            return R.error();
        }
        @ExceptionHandler(ArithmeticException.class)
        @ResponseBody
        public R error(ArithmeticException e){
            e.printStackTrace();
            return R.error().message("执行了自定义异常");
        }
    }

