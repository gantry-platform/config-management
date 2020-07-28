package ai.gantry.configmanagement.util;

import ai.gantry.configmanagement.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import ai.gantry.configmanagement.model.Error;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> defaultHandler(Exception e, HttpServletResponse res) throws Exception {
        e.printStackTrace();
        Error error = new Error();
        error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> apiExceptionHandler(Exception e, HttpServletResponse res) throws Exception {
        logger.error(e.getMessage());
        HttpStatus status = ((ApiException)e).getHttpStatus();
        Error error = new Error();
        error.setCode(status.toString());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, status);
    }
}
