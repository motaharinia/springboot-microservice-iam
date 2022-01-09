package com.motaharinia.ms.iam.external.reporter.business.service;


import com.motaharinia.ms.iam.external.reporter.presentation.AuditDto;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس بیرونی گزارشات بازدید
 */
public interface ReporterExternalService {
    /**
     * متد ثبت گزارش به ازای بازدید از سایت
     *
     * @param auditDto مدل گزارش بازدید از api های سایت
     */
    void auditCreate(AuditDto auditDto);
}
