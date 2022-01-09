package com.motaharinia.ms.iam.config.security.oauth2.enumeration;

import com.motaharinia.ms.iam.generatequery.permission.PermissionDto;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * دسترسی های سکیوریتی
 */
public final class AuthorityConstant {

    public static List<PermissionDto> permissionDtoList = new ArrayList<>();


    public static void fill() {
        //اعضای باشگاه==============================
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.FOLDER, "IAM_APU", "اعضای باشگاه"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_APP_USER_READ, "نمایش اعضای باشگاه"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_APP_USER_CREATE, "افزودن اعضای باشگاه"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_APP_USER_DELETE, "حذف اعضای باشگاه"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_APP_USER_UPDATE, "ویرایش اعضای باشگاه"));
        //permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_APP_USER_ADD_POINT, " افزودن امتیاز اعضای باشگاه"));//بپرسم که بالاسر چه کنترلر و متدی باید بذارم؟

        //کاربران پنل مدیریتی========================
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.FOLDER, "IAM_BKU", "کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_READ, "نمایش کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_CREATE, "افزودن کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_DELETE, "حذف کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_UPDATE, "ویرایش کاربران پنل مدیریتی"));
        //permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_ADD_POINT, "افزودن امتیاز کاربران پنل مدیریتی"));//بپرسم که بالاسر چه کنترلر و متدی باید بذارم؟

        //نقش های کاربران پنل مدیریتی========================
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.FOLDER, "IAM_BUR", "نقش های کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_ROLE_READ, "نمایش نقش های کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_ROLE_CREATE, "افزودن نقش های کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_ROLE_DELETE, "حذف نقش های کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_ROLE_UPDATE, "ویرایش نقش های کاربران پنل مدیریتی"));

        //دسترسی های کاربران پنل مدیریتی========================
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.FOLDER, "IAM_BUP", "دسترسی های کاربران پنل مدیریتی"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_BACK_USER_PERMISSION_READ, "نمایش درخت دسترسی های کاربران پنل مدیریتی برای دسترسی های بک"));

        //تم========================
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.FOLDER, "IAM_THM", "تم"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_THEME_READ, "نمایش تم"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_THEME_CREATE, "افزودن تم"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_THEME_DELETE, "حذف تم"));
        permissionDtoList.add(new PermissionDto(false, PermissionTypeEnum.AUTHORITY, AuthorityConstant.IAM_THEME_UPDATE, "ویرایش تم"));
    }

    //اعضای باشگاه==============================
    public static final String IAM_APP_USER_READ = "ROLE_IAM_APU_RED";
    public static final String IAM_APP_USER_CREATE = "ROLE_IAM_APU_CRT";
    public static final String IAM_APP_USER_DELETE = "ROLE_IAM_APU_DEL";
    public static final String IAM_APP_USER_UPDATE = "ROLE_IAM_APU_UPD";
    //public static final String IAM_APP_USER_ADD_POINT = "ROLE_IAM_APU_ADP"; //بپرسم که بالاسر چه کنترلر و متدی باید بذارم؟


    //کاربران پنل مدیریتی========================
    public static final String IAM_BACK_USER_READ = "ROLE_IAM_BKU_RED";
    public static final String IAM_BACK_USER_CREATE = "ROLE_IAM_BKU_CRT";
    public static final String IAM_BACK_USER_DELETE = "ROLE_IAM_BKU_DEL";
    public static final String IAM_BACK_USER_UPDATE = "ROLE_IAM_BKU_UPD";
    //public static final String IAM_BACK_USER_ADD_POINT = "ROLE_IAM_BKU_ADP"; //بپرسم که بالاسر چه کنترلر و متدی باید بذارم؟

    //نقش های کاربران پنل مدیریتی========================
    public static final String IAM_BACK_USER_ROLE_READ = "ROLE_IAM_BUR_RED";
    public static final String IAM_BACK_USER_ROLE_CREATE = "ROLE_IAM_BUR_CRT";
    public static final String IAM_BACK_USER_ROLE_DELETE = "ROLE_IAM_BUR_DEL";
    public static final String IAM_BACK_USER_ROLE_UPDATE = "ROLE_IAM_BUR_UPD";

    //دسترسی های کاربران پنل مدیریتی========================
    public static final String IAM_BACK_USER_PERMISSION_READ = "ROLE_IAM_BUP_RED";

    //تم========================
    public static final String IAM_THEME_READ = "ROLE_IAM_THM_RED";
    public static final String IAM_THEME_CREATE = "ROLE_IAM_THM_CRT";
    public static final String IAM_THEME_DELETE = "ROLE_IAM_THM_DEL";
    public static final String IAM_THEME_UPDATE = "ROLE_IAM_THM_UPD";


}
