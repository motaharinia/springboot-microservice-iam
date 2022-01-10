package com.motaharinia.ms.iam.generatequery.permission;

import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس جنریت کردن کوئری پرمیژن کاربر برنامه بک
 */
public class AuthorityConstantBuilder {

    /**
     * طبق آخرین عدد پورت هر پروژه باید ست شود
     */
    private static Integer menuOrder = 4000;

    /**
     * نام فایلی که دستورات sql در آن جنریت میشود
     */
    private static final String FILE_NAME = "DML-IAM.sql";

    public static void main(String[] args) throws IOException {

        AuthorityConstant.fill();

        //تمپلیت کوئری برای دسترسی های پرنت
        String folderQueryTemplate = "INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (%s,'FOLDER','%s','%s',null,%s,0,0);\n";
        //تمپلیت کوئری برای دسترسی های پرنت که باید هیدن باشند
        String folderHiddenQueryTemplate = "INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (%s,'FOLDER','%s','%s',null,%s,0,1);\n";

        //تمپلیت کوئری برای دسترسی های فرزند
        String authorityQueryTemplate = "INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (%s,'AUTHORITY','%s','%s',@security_permission,%s,0,0);\n";
        //تمپلیت کوئری برای دسترسی های فرزند که باید هیدن باشند
        String authorityHiddenQueryTemplate = "INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (%s,'AUTHORITY','%s','%s',@security_permission,%s,0,1);\n";

        //رشته برای کانکت کردن کوئری های
        StringBuilder result = new StringBuilder();

        for (PermissionDto permissionDto : AuthorityConstant.permissionDtoList) {
            if (permissionDto.getTypeEnum().equals(PermissionTypeEnum.FOLDER)) {
                result.append("#==============================================" + permissionDto.getTitle() + "============================================\n");
                //دسترسی هیدن
                if (permissionDto.authority.equals("IAM_BUP")) {
                    result.append(String.format(folderHiddenQueryTemplate, permissionDto.getIsFront(), permissionDto.getAuthority(), permissionDto.getTitle(), menuOrder++));
                } else {
                    result.append(String.format(folderQueryTemplate, permissionDto.getIsFront(), permissionDto.getAuthority(), permissionDto.getTitle(), menuOrder++));
                }
                result.append("SELECT LAST_INSERT_ID() INTO @security_permission;\n\n");
            } else {
                //دسترسی هیدن
                if (permissionDto.authority.equals(AuthorityConstant.IAM_BACK_USER_PERMISSION_READ)) {
                    result.append(String.format(authorityHiddenQueryTemplate, permissionDto.getIsFront(), permissionDto.getAuthority(), permissionDto.getTitle(), menuOrder++));
                } else {
                    result.append(String.format(authorityQueryTemplate, permissionDto.getIsFront(), permissionDto.getAuthority(), permissionDto.getTitle(), menuOrder++));
                }
                result.append("SELECT LAST_INSERT_ID() INTO @security_permission_child;\n");
                result.append("INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);\n\n");
            }
        }

        Files.write(Paths.get("/" + FILE_NAME), result.toString().getBytes());
    }
}
