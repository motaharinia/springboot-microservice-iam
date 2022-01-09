package com.motaharinia.ms.iam.config.log.grpc;

import com.motaharinia.ms.iam.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class GrpcExceptionTranslator implements ServerInterceptor {

    @Autowired
    private ExceptionLogger exceptionLogger;

    @Autowired
    private MessageSource messageSource;

    @Override
    public <Q, S> ServerCall.Listener<Q> interceptCall(ServerCall<Q, S> serverCall, Metadata metadata, ServerCallHandler<Q, S> serverCallHandler) {
        ServerCall.Listener<Q> listener = serverCallHandler.startCall(serverCall, metadata);
        return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata, exceptionLogger, new CustomObjectMapper(messageSource));
    }

}
