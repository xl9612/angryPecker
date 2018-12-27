package com.definesys.angrypecker.exception;

import com.definesys.mpaas.common.exception.MpaasBusinessException;
import com.definesys.mpaas.common.exception.MpaasRuntimeException;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class HandleException {

    @Autowired
    private SWordLogger logger;

    /**
     * 自定义拦截业务异常
     */
    @ExceptionHandler({DragonException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response notFount(DragonException e, HttpServletRequest request) {
        logger.info("DragonException handler");
        if (e instanceof DragonException){
            return Response.error(e.getMessage());
        }
        return Response.error(e.getMessage());
    }

    /**
     * 倚天定义的业务异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = MpaasBusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response notFounte(Exception e, HttpServletRequest request) {
        logger.info("HandleException-->MpaasBusinessException");
        if (e instanceof MpaasBusinessException){
            return Response.error(e.getMessage());
        }
        return Response.error(e.getMessage());
    }

    /**
     * 运行时异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(value = MpaasRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response mpaasRuntimeException(Exception e, HttpServletRequest request) {
        logger.info("HandleException-->mpaasRuntimeException");
        if (e instanceof MpaasRuntimeException){
            return Response.error(e.getMessage());
        }
        return Response.error(e.getMessage());
    }

}
