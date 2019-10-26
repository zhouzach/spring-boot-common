//package org.rabbit.exception;
//
//import org.joda.time.DateTime;
//import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.WebRequest;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *
// * https://www.mkyong.com/spring-boot/spring-rest-error-handling-example/
// * https://stackoverflow.com/questions/38119556/change-format-timestamp-exception-spring
// */
//@Component
//public class CustomErrorAttributes extends DefaultErrorAttributes {
//
//    @Override
//    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
//
//        // Let Spring handle the error first, we will modify later :)
//        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
//
//        // format & update timestamp
//        Object timestamp = errorAttributes.get("timestamp");
//        if (timestamp == null) {
//            errorAttributes.put("timestamp", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
//        } else {
//            errorAttributes.put("timestamp", new DateTime(timestamp).toString("yyyy-MM-dd HH:mm:ss"));
//        }
//
//        errorAttributes.remove("error");
//        errorAttributes.remove("message");
//        errorAttributes.put("status", 400);
//
//        // insert a new key
//        errorAttributes.put("version", "1.0");
//        Throwable throwable = getError(webRequest);
//        Throwable cause = throwable.getCause();
//        if (cause != null) {
//            Map<String, Object> causeErrorAttributes = new HashMap<>();
//            causeErrorAttributes.put("exception", cause.getClass().getName());
//            causeErrorAttributes.put("message", cause.getMessage());
//            errorAttributes.put("cause", causeErrorAttributes);
//        }
//
//        return errorAttributes;
//
//    }
//
//}
