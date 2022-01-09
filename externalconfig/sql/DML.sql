-- Steps:
-- 1.run externalconfig/sql/DDL.sql
-- 2.run application
-- 3.run externalconfig/sql/DML.sql


INSERT INTO `ms_iam`.`back_user`
(`create_at`, `create_user_id`, `create_user_ip`, `hidden`, `invalid`, `update_at`, `update_user_id`, `update_user_ip`, `email_address`, `first_name`, `gender`, `last_name`, `mobile_no`, `national_code`)
VALUES (null, null, null, 0, 0, null, null, null, 'admin@gmail.com', 'admin', 'MALE', 'admin', '09354161222', '0083419004');
SELECT LAST_INSERT_ID() INTO @back_user;

INSERT INTO `ms_iam`.`security_user`
(`create_at`,`create_user_id`,`create_user_ip`,`hidden`,`invalid`,`update_at`,`update_user_id`,`update_user_ip`,`account_expired`,`account_locked`,
 `app_user_id`,`back_user_id`,`credential_expired`,`email_address`,`enabled`,`mobile_no`,`password`,`username`)
VALUES(null,null,null,0,0,null,null,null,0,0,null,@back_user,0,'admin@gmail.com',1,'09354161222','82f8b21d085463a63227954074e35243','0083419004');
SELECT LAST_INSERT_ID() INTO @security_user;

INSERT INTO security_role (is_front, title,invalid,hidden) VALUES (0, 'ADMIN',false,false);
SELECT LAST_INSERT_ID() INTO @security_role;

INSERT INTO security_user_jt_security_role (security_user_id, security_role_id) VALUES (@security_user, @security_role);

#==============================================IAM============================================
#==============================================اعضای باشگاه============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_APU','اعضای باشگاه',null,4000,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_RED','نمایش اعضای باشگاه',@security_permission,4001,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_CRT','افزودن اعضای باشگاه',@security_permission,4002,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_DEL','حذف اعضای باشگاه',@security_permission,4003,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_UPD','ویرایش اعضای باشگاه',@security_permission,4004,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_ADP',' افزودن امتیاز اعضای باشگاه',@security_permission,4005,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================کاربران پنل مدیریتی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BKU','کاربران پنل مدیریتی',null,4006,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_RED','نمایش کاربران پنل مدیریتی',@security_permission,4007,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_CRT','افزودن کاربران پنل مدیریتی',@security_permission,4008,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_DEL','حذف کاربران پنل مدیریتی',@security_permission,4009,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_UPD','ویرایش کاربران پنل مدیریتی',@security_permission,4010,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_ADP','افزودن امتیاز کاربران پنل مدیریتی',@security_permission,4011,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================نقش های کاربران پنل مدیریتی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUR','نقش های کاربران پنل مدیریتی',null,4012,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_RED','نمایش نقش های کاربران پنل مدیریتی',@security_permission,4013,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_CRT','افزودن نقش های کاربران پنل مدیریتی',@security_permission,4014,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_DEL','حذف نقش های کاربران پنل مدیریتی',@security_permission,4015,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_UPD','ویرایش نقش های کاربران پنل مدیریتی',@security_permission,4016,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================دسترسی های کاربران پنل مدیریتی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUP','دسترسی های کاربران پنل مدیریتی',null,4017,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUP_RED','نمایش درخت دسترسی های کاربران پنل مدیریتی برای دسترسی های بک',@security_permission,4018,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================Catalog============================================
#==============================================قرعه کشی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_LTR','قرعه کشی',null,4000,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_RED','نمایش قرعه کشی',@security_permission,4001,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_CRT','افزودن قرعه کشی',@security_permission,4002,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_DEL','حذف قرعه کشی',@security_permission,4003,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_UPD','ویرایش قرعه کشی',@security_permission,4004,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_TRS_CRT','افزودن تراکنش قرعه کشی',@security_permission,4005,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================جوایز============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_RWD','جوایز',null,4006,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_RED','نمایش جوایز',@security_permission,4007,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_CRT','افزودنجوایز',@security_permission,4008,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_DEL','حذف جوایز',@security_permission,4009,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_UPD','ویرایش جوایز',@security_permission,4010,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================دسته بندی جوایز============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_CAT','دسته بندی جوایز',null,4011,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAT_RED','نمایش دسته بندی جوایز',@security_permission,4012,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAT_CRT','افزودن دسته بندی جوایز',@security_permission,4013,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAT_DEL','حذف دسته بندی جوایز',@security_permission,4014,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAT_UPD','ویرایش دسته بندی جوایز',@security_permission,4015,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================تخفیف ها============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_DIS','تخفیف ها',null,4016,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_DIS_RED','نمایش تخفیف ها',@security_permission,4017,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_DIS_CRT','افزودن تخفیف ها',@security_permission,4018,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_DIS_DEL','حذف تخفیف ها',@security_permission,4019,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_DIS_UPD','ویرایش تخفیف ها',@security_permission,4020,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================شرایط فروش============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_TRM_SAL','شرایط فروش',null,4021,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_TRM_SAL_RED','نمایش شرایط فروش',@security_permission,4022,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_TRM_SAL_CRT','افزودن شرایط فروش',@security_permission,4023,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_TRM_SAL_DEL','حذف شرایط فروش',@security_permission,4024,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_TRM_SAL_UPD','ویرایش شرایط فروش',@security_permission,4025,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================خرید============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_PUR','خرید',null,4026,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_PUR_RED','نمایش خرید',@security_permission,4027,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_PUR_CRT','افزودن خرید',@security_permission,4028,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================انبارداری============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CATALOG_STK','انبارداری',null,4029,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_STK_RED','نمایش انبارداری',@security_permission,4030,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_STK_UPD','ویرایش انبارداری',@security_permission,4031,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);


#==============================================User Panel============================================
#==============================================اعضای باشگاه============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_APU','اعضای باشگاه',null,5000,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_RED','نمایش اعضای باشگاه',@security_permission,5001,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================دسترسی های کاربران پنل مدیریتی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUP','دسترسی های کاربران پنل مدیریتی',null,5002,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUP_RED','نمایش درخت دسترسی های کاربران پنل مدیریتی برای دسترسی های بک',@security_permission,5003,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================بلاگ============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','USRP_BLG','بلاگ',null,5004,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_BLG_RED','نمایش بلاگ',@security_permission,5005,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_BLG_CRT','افزودن بلاگ',@security_permission,5006,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_BLG_DEL','حذف بلاگ',@security_permission,5007,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_BLG_UPD','ویرایش بلاگ',@security_permission,5008,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================پیام============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','USRP_MSG','پیام',null,5009,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_MSG_RED','نمایش پیام',@security_permission,5010,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_MSG_CRT','افزودن پیام',@security_permission,5011,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_MSG_DEL','حذف پیام',@security_permission,5012,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_MSG_UPD','ویرایش پیام',@security_permission,5013,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_WLT_RED','نمایش کیف پول',@security_permission,5014,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_USRP_DSH_RED','نمایش داشبورد',@security_permission,5015,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================Point Tracker============================================
#==============================================اعضای باشگاه============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_APU','اعضای باشگاه',null,9000,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_RED','نمایش اعضای باشگاه',@security_permission,9001,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================دسترسی های کاربران پنل مدیریتی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUP','دسترسی های کاربران پنل مدیریتی',null,9002,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUP_RED','نمایش درخت دسترسی های کاربران پنل مدیریتی برای دسترسی های بک',@security_permission,9003,0,1);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================سطح============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_TIR','سطح',null,9004,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_RED','نمایش سطح',@security_permission,9005,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_CRT','افزودن سطح',@security_permission,9006,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_DEL','حذف سطح',@security_permission,9007,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_UPD','ویرایش سطح',@security_permission,9008,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================پیام============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_SMS','پیام',null,9009,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SMS_RED','نمایش پیام',@security_permission,9010,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SMS_CRT','افزودن پیام',@security_permission,9011,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SMS_DEL','حذف پیام',@security_permission,9012,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SMS_UPD','ویرایش پیام',@security_permission,9013,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================بازی============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_GAM','بازی',null,9014,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_RED','نمایش بازی',@security_permission,9015,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_CRT','افزودن بازی',@security_permission,9016,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_DEL','حذف بازی',@security_permission,9017,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_UPD','ویرایش بازی',@security_permission,9018,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_PLY','انجام بازی',@security_permission,9019,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================پیشنهاد امتیاز============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_SUG','پیشنهاد امتیاز',null,9020,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_RED','نمایش پیشنهاد امتیاز',@security_permission,9021,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_CRT','افزودن پیشنهاد امتیاز',@security_permission,9022,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_DEL','حذف پیشنهاد امتیاز',@security_permission,9023,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_UPD','ویرایش پیشنهاد امتیاز',@security_permission,9024,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_INC','افزایش پیشنهاد امتیاز',@security_permission,9025,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

#==============================================امتیاز سالانه============================================
INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_ANP','امتیاز سالانه',null,9026,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission;

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_RED','نمایش امتیاز سالانه',@security_permission,9027,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_DEL','حذف امتیاز سالانه',@security_permission,9028,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_UPD','ویرایش امتیاز سالانه',@security_permission,9029,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_PBL_RED','نمایش امتیاز ',@security_permission,9030,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_PTR_RED','نمایش تراکنش امتیاز',@security_permission,9031,0,0);
SELECT LAST_INSERT_ID() INTO @security_permission_child;
INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

# #==============================================Old query
# # security permission
# #==============================================
# SET @menuOrder := 0; -- Define a variable
#
# # security permission(ms_iam)(AppUser)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_APU','اعضای باشگاه',null, (@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_APU_ADP','افزودن امتیاز',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_pointtracker)(Tier)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_TIR','سطوح اعضای باشگاه',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_TIR_ADP','افزودن امتیاز',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_pointtracker)(Lottery)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_LTR','قرعه کشی',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_LTR_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_pointtracker)(Game)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_GAM','بازی های باشگاه مشتریان',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_GAM_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_iam)(BackUser)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BKU','کاربران پنل مدیریتی',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BKU_ADP','افزودن امتیاز',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_catalog)(Reward)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CTL_RWD','جوایز',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_RWD_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_catalog)(Category Reward)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','CTL_CAR','دسته بندی جوایز',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAR_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAR_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAR_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_CTL_CAR_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
#
# # security permission(ms_???)(Report)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','???_RPT','گزارشات',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_???_RPT_POT','گزارشات امتیازات',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_???_RPT_SAL','گزارشات فروش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_???_RPT_TAX','گزارش مالیات بر ارزش افزوده',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_iam)(Back User Role)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUR','نقش های کاربران پنل مدیریتی',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUR_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_iam)(Back User Permission)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','IAM_BUP','دسترسی های کاربران پنل مدیریتی',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_IAM_BUP_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,1);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_pointtracker)(SUGGESTION)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_SUG','افزودن امتیازهای پیشنهادی باشگاه مشتریان',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_CRT','افزودن',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_SUG_INC','افزایش امتیاز با اعتبار کیف پول',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# # security permission(ms_pointtracker)(ANNUAL POINT)
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'FOLDER','POT_ANP','تنظیم امتیازات',null,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission;
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_RED','نمایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_DEL','حذف',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);
#
# INSERT INTO `ms_iam`.`security_permission` (is_front,type_enum,authority,title,parent_id,menu_order,invalid,hidden) VALUES (false,'AUTHORITY','ROLE_POT_ANP_UPD','ویرایش',@security_permission,(@menuOrder := @menuOrder + 1),0,0);
# SELECT LAST_INSERT_ID() INTO @security_permission_child;
# INSERT INTO security_role_jt_security_permission (security_permission_id, security_role_id) VALUES (@security_permission_child, @security_role);

