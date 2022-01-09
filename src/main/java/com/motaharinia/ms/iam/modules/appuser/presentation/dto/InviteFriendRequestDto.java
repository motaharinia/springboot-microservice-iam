package com.motaharinia.ms.iam.modules.appuser.presentation.dto;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * کلاس مدل دعوت از دوستان
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteFriendRequestDto {
    /**
     * لیست شماره موبایل هایی که میخواهیم برایشان کد معرف را ارسال کنیم
     */
    @Required
    Set<String> mobileNoToSet = new HashSet<>();
}
