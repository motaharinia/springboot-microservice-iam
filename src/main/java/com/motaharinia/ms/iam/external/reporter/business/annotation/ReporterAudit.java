package com.motaharinia.ms.iam.external.reporter.business.annotation;

import java.lang.annotation.*;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انوتیشن بازرسی بازدید متد یک Api
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReporterAudit {
}