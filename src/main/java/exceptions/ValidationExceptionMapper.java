package exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.ArrayList;
import java.util.List;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<Violation> violations = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String[] fieldParts = field.split("\\.");
            String simpleField = fieldParts[fieldParts.length - 1];
            String message = violation.getMessage();
            violations.add(new Violation(simpleField, message));
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(violations))
                .build();
    }
}