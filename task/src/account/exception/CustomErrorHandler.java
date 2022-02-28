package account.exception;

import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;

//@ControllerAdvice
//public class CustomErrorHandler {
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<CustomError> handleConstraintViolationException(ConstraintViolationException exception,
//                                                                          ServletWebRequest webRequest) {
//        CustomError customError = new CustomError();
//        customError.setTimestamp(LocalDateTime.now().toString());
//        customError.setStatus(HttpStatus.BAD_REQUEST.value());
//        customError.setMessage(exception.getMessage());
//        customError.setError("Bad request");
//        customError.setPath(webRequest.getContextPath().toString());
//        return ResponseEntity.badRequest().body(customError);
//    }
//}

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}
