package eionet.rod.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author jrobles
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid character found in the request target")
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        this("Invalid character found in the request target : " + message, null);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
