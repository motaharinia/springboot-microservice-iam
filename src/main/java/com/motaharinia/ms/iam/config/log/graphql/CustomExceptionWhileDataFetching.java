package com.motaharinia.ms.iam.config.log.graphql;

import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static graphql.Assert.assertNotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطای کاستوم گراف کیو ال
 */
public class CustomExceptionWhileDataFetching implements GraphQLError {

    private final String message;
    private final List<Object> path;
    private final Throwable exception;
    private final List<SourceLocation> locations;
    private final Map<String, Object> extensions;

    public CustomExceptionWhileDataFetching(ExecutionPath path, Throwable exception, SourceLocation sourceLocation , ClientResponseDto<String> clientResponseDto) {
        this.path = assertNotNull(path).toList();
        this.exception = assertNotNull(exception);
        this.locations = Collections.singletonList(sourceLocation);
        this.extensions = mkExtensions(exception,clientResponseDto);
        this.message = clientResponseDto.getMessage();
    }

    private Map<String, Object> mkExtensions(Throwable exception ,ClientResponseDto<String> clientResponseDto) {
        Map<String, Object> extensions = new LinkedHashMap<>();
        if (exception instanceof GraphQLError) {
            Map<String, Object> map = ((GraphQLError) exception).getExtensions();
            if (map != null) {
                extensions.putAll(map);
            }
            extensions.put("clientResponseDto",clientResponseDto);
        }
        return extensions;
    }


    public Throwable getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    @Override
    public List<Object> getPath() {
        return path;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public String toString() {
        return "ExceptionWhileDataFetching{" +
                "path=" + path +
                "exception=" + exception +
                "locations=" + locations +
                '}';
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return GraphqlErrorHelper.equals(this, o);
    }

    @Override
    public int hashCode() {
        return GraphqlErrorHelper.hashCode(this);
    }
}
