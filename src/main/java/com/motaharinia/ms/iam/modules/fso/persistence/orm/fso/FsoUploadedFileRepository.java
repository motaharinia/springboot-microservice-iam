package com.motaharinia.ms.iam.modules.fso.persistence.orm.fso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس ریپازیتوری فایلهای آپلود شده
 */

@Repository
public interface FsoUploadedFileRepository extends JpaRepository<FsoUploadedFile, Long>, JpaSpecificationExecutorWithProjection<FsoUploadedFile> {
    FsoUploadedFile findByFileKey(String fileKey);
}
