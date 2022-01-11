package com.motaharinia.ms.iam.config.feign;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.custom.customexception.externalcall.ExternalCallException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدیریت خطای دریافتی feign و تبدیل آن به اکسپشن
 */

@RequiredArgsConstructor
@Slf4j
@Component
public class FeignErrorDecoder extends ErrorDecoder.Default {

    private final ObjectMapper objectMapper;

    /**
     * متد بررسی و تبدیل ریسپانس دریافتی به اکسپشن استاندارد
     *
     * @param methodKey کلید متد
     * @param response  ریسپانس
     * @return خروجی: اکسپشن
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("FeignErrorDecoder.decode methodKey:{} response.reason():{} response.status:{}", methodKey, response.reason(), response.status());

        if (!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.status())) {
            switch (HttpStatus.valueOf(response.status())) {
                case BAD_GATEWAY:
                case GATEWAY_TIMEOUT:
                case SERVICE_UNAVAILABLE:
                case INTERNAL_SERVER_ERROR:
                    return new ExternalCallException(getClass(), response.request().url(), getHttpMethod(response.request().httpMethod()), FeignCallEnum.getRequestNo(methodKey), String.valueOf(response.status()), "I/O error. Connection refused: connect", new Exception(response.reason()));
                case NOT_FOUND:
                    return new ExternalCallException(getClass(), response.request().url(), getHttpMethod(response.request().httpMethod()), FeignCallEnum.getRequestNo(methodKey), String.valueOf(response.status()), "Not Found", null);
                case UNPROCESSABLE_ENTITY:
                case BAD_REQUEST:
                    ClientResponseDto<String> clientResponseDto = readFailedResponse(response);
                    throw new ExternalCallException(getClass(), response.request().url(), getHttpMethod(response.request().httpMethod()), FeignCallEnum.getRequestNo(methodKey), String.valueOf(response.status()), clientResponseDto.getException().getMessage(), new Exception(response.reason()));
                default:
                    return new Exception(response.reason());

            }
        }

        return super.decode(methodKey, response);
    }

    /**
     * متد تبدیل ریسپانس به شیی خروجی فرانت
     *
     * @param response ریسپانس
     * @return خروجی: شیی خروجی فرانت
     */
    @Nullable
    private ClientResponseDto<String> readFailedResponse(Response response) {
        try {
            return objectMapper.readValue(response.body().asReader(StandardCharsets.UTF_8), ClientResponseDto.class);
        } catch (IOException e) {
            log.error("couldn't parse readFailedResponse: {}", response);
            return null;
        }
    }

    /**
     * متد تبدیل مقدار ثابت به شیی نوع فراخوانی وب
     *
     * @param httpMethod مقدار ثابت
     * @return خروجی: شیی نوع فراخوانی وب
     */
    private HttpMethod getHttpMethod(@NotNull Request.HttpMethod httpMethod) {
        switch (httpMethod) {
            case POST:
                return HttpMethod.POST;
            case GET:
                return HttpMethod.GET;
            case PUT:
                return HttpMethod.PUT;
            case DELETE:
                return HttpMethod.DELETE;
            case HEAD:
                return HttpMethod.HEAD;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            case PATCH:
                return HttpMethod.PATCH;
            case TRACE:
                return HttpMethod.TRACE;
            default:
                return null;
        }
    }

}
