package exceptions;

import java.util.List;

public class ErrorResponse {
    private String message;
    private List<Violation> violations;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(List<Violation> violations) {
        this.violations = violations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}