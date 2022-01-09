package com.motaharinia.ms.iam.modules.theme.persistence.odm;

import com.motaharinia.msjpautility.document.CustomDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashMap;

/**
 * کلاس داکیومنت تم
 */
@Document(collection = "theme_document")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ThemeDocument extends CustomDocument implements Serializable {

    @Id
    private Long  id;

    /**
     * عنوان
     */
    @Field("title")
    private String title;

    /**
     * هش مپ تنظیمات تم
     */
    @Field("setting")
    private HashMap<String,String> settingHashMap = new HashMap<>();

    /**
     * آیا تم پیشفرض است
     */
    @Field("is_default")
    private Boolean isDefault = false;

}
