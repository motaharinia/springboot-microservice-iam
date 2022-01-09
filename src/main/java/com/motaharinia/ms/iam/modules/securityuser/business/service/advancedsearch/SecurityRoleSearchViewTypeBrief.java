package com.motaharinia.ms.iam.modules.securityuser.business.service.advancedsearch;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRole;
import com.motaharinia.msjpautility.search.SearchRowView;
import com.motaharinia.msjpautility.search.annotation.SearchDataColumn;

/**
 * برای جستجو پیشرفته میباشد. در حال حاضر در پروژه استفاده نمیشود
 */
@JsonDeserialize(as = SecurityRole.class)
public interface SecurityRoleSearchViewTypeBrief extends SearchRowView {

    @SearchDataColumn(index = 1, name = "id")
    Integer getId();


    @SearchDataColumn(index = 2, name = "title")
    String geTitle();

    @Override
    default String toOut() {
        return this.getId() + "," + this.geTitle() ;
    }
}
