package com.motaharinia.ms.iam.modules.fso.business.enumeration;


/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت عنوان (شامل نام و نوع فایل مربوط به انتیتی) و مقدار (مسیر قرار گیری فایلهای ماژول)
 */
public enum FsoModuleEnum {
    /**
     * تصویر پروفایل عضو
     */
    USER_PROFILE_IMAGE("/userpanel/user/%ENTITYID%/profileimage/"),


    /**
     * تصویر پروفایل عضو
     */
    PUBLIC_MESSAGE_ATTACHMENT_FILE("/userpanel/publicmessage/%ENTITYID%/attachment/"),
    /**
     * تصویر آپلود شده برای پست
     */
    BLOG_UPLOADED_FILE("/loyalty/blog/%ENTITYID%/blogFile/"),
    ;
    ;
    private final String value;
    private static final String ENTITY_ID="%ENTITYID%";

    FsoModuleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getEntityKindDirectoryPath(Long entityId) {
        //example: "/eshop/product/27/image3dfile/"
        return this.getValue().replace(ENTITY_ID, entityId.toString());
    }

    public String getEntityDirectoryPath() {
        int entityIdIndex = this.getValue().indexOf(ENTITY_ID);
        //example: "/eshop/product/"
        return this.getValue().substring(0, entityIdIndex);
    }

    public String getKindDirectoryPath(Long entityId) {
        //example: "/eshop/product/27/image3dfile/"
        String entityKindDirectoryPath = getEntityKindDirectoryPath(entityId);
        int entityIdIndex = this.getValue().indexOf(ENTITY_ID);
        //example: "/eshop/product/"
        String entityDirectoryPath = this.getValue().substring(0, entityIdIndex);
        //example: "/27/image3dfile/"
        return "/" + entityKindDirectoryPath.replace(entityDirectoryPath, "");
    }

    public String getKindFolderName() {
        String[] tempArray = this.getValue().split("/", -1);
        if (tempArray.length > 1) {
            return tempArray[tempArray.length - 2];
        } else {
            return "";
        }
    }
}
