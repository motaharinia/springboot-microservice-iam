package com.motaharinia.ms.iam.config.log.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.ms.iam.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class ExceptionHandlingServerCallListener<Q, S> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Q> {

    /**
     * شییی لاگ خطاها
     */
    private final ExceptionLogger exceptionLogger;
    private final ServerCall<Q, S> serverCall;
    private final Metadata metadata;

    private final ObjectMapper objectMapper;

    ExceptionHandlingServerCallListener(ServerCall.Listener<Q> listener, ServerCall<Q, S> serverCall, Metadata metadata, ExceptionLogger exceptionLogger, ObjectMapper objectMapper) {
        super(listener);
        this.serverCall = serverCall;
        this.metadata = metadata;
        this.exceptionLogger = exceptionLogger;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onHalfClose() {
        try {
            super.onHalfClose();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
        }
    }

    @Override
    public void onReady() {
        try {
            super.onReady();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
        }
    }

    private void handleException(RuntimeException exception, ServerCall<Q, S> serverCall, Metadata metadata) {
        try {
            //تنظیم زبان لوکیل پروژه روی پارسی
            Locale.setDefault(new Locale("fa", "IR"));
            ClientResponseDto<String> clientResponseDto = exceptionLogger.handle(exception, null, null);
            switch (clientResponseDto.getException().getType()) {
                case BUSINESS_EXCEPTION:
                    serverCall.close(Status.UNKNOWN.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case GENERAL_EXCEPTION:
                    serverCall.close(Status.INTERNAL.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case EXTERNAL_CALL_EXCEPTION:
                    serverCall.close(Status.UNAVAILABLE.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case VALIDATION_EXCEPTION:
                    serverCall.close(Status.INVALID_ARGUMENT.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                default:
                    serverCall.close(Status.UNIMPLEMENTED.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);

            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("UM jsonProcessingException: {}", jsonProcessingException.getMessage() + " , " + jsonProcessingException);
        }
    }

}
