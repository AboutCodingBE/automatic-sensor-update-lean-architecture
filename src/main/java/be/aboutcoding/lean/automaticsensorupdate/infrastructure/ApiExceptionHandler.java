package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleMaxSizeException(RuntimeException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Failed to retrieve status. Reasons: %s", exc.getMessage()));
    }
}
