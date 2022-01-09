package com.motaharinia.ms.iam.config.log.graphql;


import com.motaharinia.ms.iam.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import graphql.language.SourceLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدیریت خطای کاستوم گراف کیو ال
 */
@Component
@Slf4j
public class GraphqlExceptionTranslator implements DataFetcherExceptionHandler {
    /**
     * شییی لاگ خطاها
     */
    private final ExceptionLogger exceptionLogger;
    /**
     * شیی درخواست وب
     */
    private final HttpServletRequest httpServletRequest;
    /**
     * شیی پاسخ وب
     */
    private final HttpServletResponse httpServletResponse;


    public GraphqlExceptionTranslator(ExceptionLogger exceptionLogger, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.exceptionLogger = exceptionLogger;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void accept(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable throwable = handlerParameters.getException();
        SourceLocation sourceLocation = handlerParameters.getField().getSourceLocation();
        ExecutionPath path = handlerParameters.getPath();
        if (throwable instanceof Exception) {

            ClientResponseDto<String> clientResponseDto= exceptionLogger.handle((Exception) throwable,httpServletRequest,httpServletResponse);

            CustomExceptionWhileDataFetching error = new CustomExceptionWhileDataFetching(path, throwable, sourceLocation, clientResponseDto);
            handlerParameters.getExecutionContext().addError(error);
        } else {
            //اگر خطای دریافت شده از نوع اکسپشن نبود و توسط سرویس ها به صورت اکسپشن پرتاب نشده بود طبق روش پیش فرض گراف کیو ال خروجی داده میشود
            ExceptionWhileDataFetching error = new ExceptionWhileDataFetching(path, throwable, sourceLocation);
            handlerParameters.getExecutionContext().addError(error);
        }
    }

}
