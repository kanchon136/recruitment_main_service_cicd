//package com.cd.recruitment_requisition_service.cumtomException;
//
//import com.cd.recruitment_requisition_service.enums.ResponseEnum;
//import com.cd.recruitment_requisition_service.utils.BaseResponse;
//import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.support.DefaultMessageSourceResolvable;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageConversionException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.resource.NoResourceFoundException;
//
//import java.io.FileNotFoundException;
//import java.security.NoSuchAlgorithmException;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(NoResourceFoundException.class)
//    public void handleNoResourceFoundException(NoResourceFoundException ex) throws NoResourceFoundException {
//        throw ex;
//    }
//
//    @ExceptionHandler(DataAccessException.class)
//    public ResponseEntity<BaseResponse> handleDataAccessException(DataAccessException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<BaseResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.NOTFOUND.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<BaseResponse> handleResouceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//
//
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<BaseResponse> handleNullPointerException(NullPointerException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<BaseResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(OutOfMemoryError.class)
//    public ResponseEntity<BaseResponse> handleOutOfMemoryError(OutOfMemoryError ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(ArithmeticException.class)
//    public ResponseEntity<BaseResponse> handleArithmeticException(ArithmeticException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<BaseResponse> handleCustomException(CustomException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ex.getMessage());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(NumberFormatException.class)
//    public ResponseEntity<BaseResponse> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(HttpClientErrorException.class)
//    public ResponseEntity<BaseResponse> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode().value()));
//    }
//
//    @ExceptionHandler(FileNotFoundException.class)
//    public ResponseEntity<BaseResponse> handleFileNotFoundException(FileNotFoundException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(IndexOutOfBoundsException.class)
//    public ResponseEntity<BaseResponse> handleIndexOutOfBoundsException(IndexOutOfBoundsException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(UnsupportedOperationException.class)
//    public ResponseEntity<BaseResponse> handleUnsupportedOperationException(UnsupportedOperationException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
//    }
//
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<BaseResponse> handleGeneralException(Exception ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @ExceptionHandler(HttpMessageConversionException.class)
//    public ResponseEntity<BaseResponse> handleHttpMessageConversionException(HttpMessageConversionException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // You can choose a different status code if appropriate
//    }
//
//    @ExceptionHandler(InvalidDefinitionException.class)
//    public ResponseEntity<BaseResponse> handleInvalidDefinitionException(InvalidDefinitionException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error(ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // You can choose a different status code if appropriate
//    }
//
//
////    @ExceptionHandler(ConstraintViolationException.class)
////    public ResponseEntity<BaseResponse> handleConstraintViolationException(ConstraintViolationException ex) {
////        Map<String, String> errors = new HashMap<>();
////
////        ex.getConstraintViolations().forEach(violation -> {
////            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
////        });
////        log.error("Validation errors: {}", errors.toString());
////        BaseResponse response = new BaseResponse();
////        response.setMessage(ResponseEnum.FAILED.getStatus());
////        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
////    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<BaseResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error("DataIntegrityViolation: {}", ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//
//    }
//
//    @ExceptionHandler(NoSuchAlgorithmException.class)
//    public ResponseEntity<BaseResponse> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex, WebRequest request) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        log.error("NoSuchAlgorithmException: {}", ex.getMessage(), ex);
//        return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage(ResponseEnum.FAILED.getStatus());
//        String errorMessage = exception.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .findFirst()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .orElse("Invalid input");
//
//        log.error("Validation error: {}", errorMessage, exception);
//
//        response.setErrorData(errorMessage);
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
//    public ResponseEntity<BaseResponse> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage("Requested data not found in database");
//        log.error("Entity Not Found: {}", ex.getMessage());
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(com.fasterxml.jackson.databind.exc.MismatchedInputException.class)
//    public ResponseEntity<BaseResponse> handleMismatchedInputException(com.fasterxml.jackson.databind.exc.MismatchedInputException ex) {
//        BaseResponse response = new BaseResponse();
//        response.setMessage("Invalid input format: Expected a list but received a single value or different type.");
//        log.error("Jackson Mismatched Input: {}", ex.getMessage());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//
//}
