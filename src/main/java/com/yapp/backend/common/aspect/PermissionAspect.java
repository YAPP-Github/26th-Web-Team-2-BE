package com.yapp.backend.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yapp.backend.common.annotation.RequirePermission;
import com.yapp.backend.service.authorization.PermissionService;
import com.yapp.backend.filter.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 권한 검증을 위한 AOP 어드바이스
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {
    
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;
    
    /**
     * @RequirePermission 어노테이션이 붙은 메서드 실행 전 권한 검증
     */
    @Before("@annotation(com.yapp.backend.common.annotation.RequirePermission)")
    public void validatePermission(JoinPoint joinPoint) {
        try {
            // 1. 어노테이션 정보 추출
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // 2. 현재 사용자 정보 추출
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
                throw new AuthenticationCredentialsNotFoundException("인증 정보를 찾을 수 없습니다.");
            }
            Long userId = userDetails.getUserId();

            // 3. 리소스 ID들 추출
            Map<String, Long> resourceIds = extractResourceIds(joinPoint, method, annotation);

            // 4. 기본 권한 검증 수행
            permissionService.validatePermission(annotation, resourceIds, userId);
            
            log.debug("권한 검증 성공: method={}, userId={}, resourceIds={}", 
                    method.getName(), userId, resourceIds);
                    
        } catch (Exception e) {
            log.error("권한 검증 실패: method={}, error={}", 
                    joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * 메서드 파라미터에서 리소스 ID들을 추출합니다.
     */
    private Map<String, Long> extractResourceIds(
            JoinPoint joinPoint,
            Method method,
            RequirePermission annotation
    ) {
        Map<String, Long> resourceIds = new HashMap<>();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        
        // 1. PathVariable에서 기본 파라미터 추출
        String mainParamName = annotation.paramName();
        try {
            Long mainResourceId = extractSingleResourceId(args, parameters, mainParamName);
            resourceIds.put(mainParamName, mainResourceId);
        } catch (IllegalArgumentException e) {
            // PathVariable에서 찾을 수 없는 경우, RequestBody에서 찾기
            log.debug("PathVariable에서 {}를 찾을 수 없어 RequestBody에서 찾습니다.", mainParamName);
        }
        
        // 3. RequestBody에서 ID 추출
        String requestBodyField = annotation.requestBodyField();
        if (!requestBodyField.isEmpty()) {
            Long requestBodyId = extractIdFromRequestBody(args, parameters, requestBodyField);
            resourceIds.put(requestBodyField, requestBodyId);
        }
        return resourceIds;
    }
    
    /**
     * 단일 리소스 ID를 추출합니다.
     */
    private Long extractSingleResourceId(Object[] args, Parameter[] parameters, String paramName) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            
            // 1. PathVariable에서 찾기
            if (parameter.getName().equals(paramName) && 
                parameter.isAnnotationPresent(PathVariable.class)) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                } else {
                    throw new IllegalArgumentException("리소스 ID 파라미터가 올바른 타입이 아닙니다: " + arg);
                }
            }
            
            // 2. RequestParam에서 찾기
            if (parameter.getName().equals(paramName) && 
                parameter.isAnnotationPresent(RequestParam.class)) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                } else {
                    throw new IllegalArgumentException("리소스 ID 파라미터가 올바른 타입이 아닙니다: " + arg);
                }
            }
        }
        
        throw new IllegalArgumentException("리소스 ID 파라미터를 찾을 수 없습니다: " + paramName);
    }
    
    /**
     * RequestBody에서 ID를 추출합니다.
     */
    private Long extractIdFromRequestBody(Object[] args, Parameter[] parameters, String fieldName) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            
            // @RequestBody 어노테이션이 있는 파라미터 찾기
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                Object requestBody = args[i];
                // ObjectMapper를 사용하여 JSON 필드 추출
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(requestBody);
                    Map<String, Object> map = objectMapper.readValue(json, Map.class);
                    Object fieldValue = map.get(fieldName);
                    if (fieldValue instanceof Number) {
                        return ((Number) fieldValue).longValue();
                    }
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("RequestBody에서 ID를 추출할 수 없습니다: " + fieldName, e);
                }
            }
        }
        throw new IllegalArgumentException("RequestBody에서 ID를 찾을 수 없습니다: " + fieldName);
    }
}