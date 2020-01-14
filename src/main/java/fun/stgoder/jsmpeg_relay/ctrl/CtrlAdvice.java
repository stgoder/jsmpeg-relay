package fun.stgoder.jsmpeg_relay.ctrl;

import fun.stgoder.jsmpeg_relay.common.Code;
import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.exception.BaseException;
import fun.stgoder.jsmpeg_relay.common.model.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CtrlAdvice {
    private Logger logger = LoggerFactory.getLogger(CtrlAdvice.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exceptionHandler(Exception e, HttpServletRequest request) {
        if (Constants.TEST_MODE)
            logger.error("exceptionHandler", e);
        String uri = request.getRequestURI();
        if (uri.contains("/rest")) {
            int code = Code.REQUEST_ERR;
            String message = e.getMessage();
            if (e instanceof BaseException) {
                BaseException baseException = (BaseException) e;
                code = baseException.code();
                message = baseException.message();
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                    .body(new Resp(code, message));
        } else {
            int code = Code.REQUEST_ERR;
            String message = e.getMessage();
            if (e instanceof BaseException) {
                BaseException baseException = (BaseException) e;
                code = baseException.code();
                message = baseException.message();
            }
            ModelAndView mv = new ModelAndView("err");
            mv.addObject("code", code);
            mv.addObject("title", message);
            mv.addObject("msg", message);
            return mv;
        }
    }
}
