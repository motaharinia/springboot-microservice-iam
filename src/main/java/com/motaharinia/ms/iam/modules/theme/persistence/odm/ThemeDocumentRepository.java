package com.motaharinia.ms.iam.modules.theme.persistence.odm;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 *  کلاس ریپازیتوری تم
 */
@Repository
public interface ThemeDocumentRepository extends MongoRepository<ThemeDocument, Long> {

    Optional<ThemeDocument> findByTitle(@NotNull String title);

    List<ThemeDocument> findByIdNot(@NotNull Long id);

    Optional<ThemeDocument> findByIsDefaultIsTrue();
}
