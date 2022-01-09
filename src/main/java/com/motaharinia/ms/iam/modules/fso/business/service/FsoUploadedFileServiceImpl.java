package com.motaharinia.ms.iam.modules.fso.business.service;


import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.ms.iam.modules.fso.business.exception.FsoException;
import com.motaharinia.ms.iam.modules.fso.business.mapper.FsoUploadedFileMapper;
import com.motaharinia.ms.iam.modules.fso.persistence.orm.fso.FsoUploadedFile;
import com.motaharinia.ms.iam.modules.fso.persistence.orm.fso.FsoUploadedFileRepository;
import com.motaharinia.ms.iam.modules.fso.presentation.FsoUploadedFileDto;
import com.motaharinia.ms.iam.modules.fso.presentation.frontuploader.FineUploaderChunkDto;
import com.motaharinia.ms.iam.modules.fso.presentation.validation.FsoSettingDto;
import com.motaharinia.msutility.custom.customexception.utility.UtilityException;
import com.motaharinia.msutility.custom.customexception.utility.UtilityExceptionKeyEnum;
import com.motaharinia.msutility.tools.fso.FsoTools;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس فایلهای آپلود شده
 */
@Qualifier("LogUploadedFileServiceImpl")
@Service
@Transactional(rollbackFor = Exception.class)
public class FsoUploadedFileServiceImpl implements FsoUploadedFileService {

    /**
     * مسیر موقت جهت آپلود فایلهای پروزه
     */
    @Value("${fso.path.upload.directory:/api/v1.0/fso/uploaded}")
    private String fsoPathUploadDirectory;

    /**
     * ریپازیتوری ادمین
     */
    private final FsoUploadedFileRepository fsoUploadedFileRepository;

    /**
     * کلاس مپر فایلهای آپلود شده
     */
    private final FsoUploadedFileMapper mapper;

    /**
     * متد سازنده
     */
    public FsoUploadedFileServiceImpl(FsoUploadedFileRepository fsoUploadedFileRepository, FsoUploadedFileMapper mapper) {
        this.fsoUploadedFileRepository = fsoUploadedFileRepository;
        this.mapper = mapper;
    }
    private static final String BUSINESS_EXCEPTION_FSO_UPLOADED_INVALID_FILE_KEY = "BUSINESS_EXCEPTION.FSO_UPLOADED_INVALID_FILE_KEY";
    private static final String BUSINESS_EXCEPTION_FSO_UPLOADED_SETTING_NOT_FOUND = "BUSINESS_EXCEPTION.FSO_UPLOADED_SETTING_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_SIZE_VALIDATION = "BUSINESS_EXCEPTION.FSO_UPLOADED_FILE_SIZE_VALIDATION";
    private static final String BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_TYPE_VALIDATION = "BUSINESS_EXCEPTION.FSO_UPLOADED_FILE_TYPE_VALIDATION";


    /**
     * این متد قسمتی از اطلاعات آپلود مربوط به فرانت پنل کلاینت ساید را از ورودی دریافت میکند و فایل و اطلاعات دیتابیسی آن را ذخیره مینماید
     *
     * @param fineUploaderChunkDto مدل داده ارسالی از کلاینت
     * @return خروجی: مدل اطلاعات فایل آپلود شده
     * @throws IOException خطا
     */
    @Override
    public FsoUploadedFileDto uploadToFileDto(FineUploaderChunkDto fineUploaderChunkDto) throws IOException {



        //تعریف و ساخت پوشه ای برای فایلهای در حال آپلود"uploading" در پوشه آپلود
        String uploadingDirectory = fsoPathUploadDirectory + "/" + "uploading";
        FsoTools.createDirectoryIfNotExist(uploadingDirectory);

        //مسیر فایل در حال آپلود طبق کلید فایل که از سمت کلاینت می آبد
        String uploadingFilePath = String.format("%s/%s", uploadingDirectory, fineUploaderChunkDto.getFileKey());
        File uploadingFile = new File(uploadingFilePath);
        FileUtils.writeByteArrayToFile(uploadingFile, fineUploaderChunkDto.getFileByteArray(), true);


        //ایجاد مدل فایل آپلود
        FsoUploadedFileDto fsoUploadedFileDto = new FsoUploadedFileDto();
        if (fineUploaderChunkDto.getFileChunkCount() - 1 == fineUploaderChunkDto.getFileChunkIndex()) {
            fsoUploadedFileDto.setFileKey(fineUploaderChunkDto.getFileKey());
            fsoUploadedFileDto.setFileByteArray(FileUtils.readFileToByteArray(uploadingFile));
            fsoUploadedFileDto.setFileSize((long) fsoUploadedFileDto.getFileByteArray().length);
            fsoUploadedFileDto.setFileUploadDateTime(new Date());
            fineUploaderChunkDto.setFileFullName(this.fixFailedFileNameCharacter(fineUploaderChunkDto.getFileFullName()));
            fsoUploadedFileDto.setFileFullName(fineUploaderChunkDto.getFileFullName());
            fsoUploadedFileDto.setFileExtension(FsoTools.getFileExtension(fineUploaderChunkDto.getFileFullName()));
            fsoUploadedFileDto.setFileName(FsoTools.getFileNameWithoutExtension(fineUploaderChunkDto.getFileFullName()));
            fsoUploadedFileDto.setFileSubSystem(fineUploaderChunkDto.getFileSubSystem());
            fsoUploadedFileDto.setFileEntity(fineUploaderChunkDto.getFileEntity());
            fsoUploadedFileDto.setFileKind(fineUploaderChunkDto.getFileEntity());

            // بررسی وجود و دریافت تنظمیات آپلود فایل مطابق با نام زیرسیستم و انتیتی و نوع فایل
            FsoSettingDto fsoSettingDto = this.getSetting(fineUploaderChunkDto.getFileSubSystem(), fineUploaderChunkDto.getFileEntity(), fineUploaderChunkDto.getFileKind());
            //اعتبارسنجی حجم فایل
            if (fsoUploadedFileDto.getFileSize() > fsoSettingDto.getSize()) {
                throw new FsoException(fineUploaderChunkDto.getFileKey(), BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_SIZE_VALIDATION, "");
            }
            //اعتبارسنجی نوع فایل
            if (fsoSettingDto.getTypeSet().stream().noneMatch(item -> item.getValue().contains(fsoUploadedFileDto.getFileExtension().toUpperCase(Locale.ROOT)))) {
                throw new FsoException(fineUploaderChunkDto.getFileKey(), BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_TYPE_VALIDATION, "");
            }

            //ذخیره فایل بر روی فایل سیستم از روی مدل دیتابیسی فایل آپلود شده و ذخیره اطلاعات در دیتابیس از روی مدل دیتابیسی فایل آپلود شده
            this.create(this.saveUploadedFile(fsoUploadedFileDto, fsoUploadedFileDto.getFileKey()));

            return fsoUploadedFileDto;
        } else {
            return null;
        }
    }

    /**
     * این متد یک لاگ دیتابیس از اطلاعات فایل آپلود شده در دیتابیس ذخیره مینماید
     *
     * @param fsoUploadedFileDto مدل فایل آپلود شده
     * @return خروجی: مدل فایل آپلود شده
     */
    @Override
    public FsoUploadedFileDto create(FsoUploadedFileDto fsoUploadedFileDto) {
        //ساخت انتیتی فایل آپلود شده از مدل فایل آپلود شده
        FsoUploadedFile fsoUploadedFile = new FsoUploadedFile();
        fsoUploadedFile.setFileExtension(fsoUploadedFileDto.getFileExtension());
        fsoUploadedFile.setFileFullName(fsoUploadedFileDto.getFileFullName());
        fsoUploadedFile.setFileKey(fsoUploadedFileDto.getFileKey());
        fsoUploadedFile.setFileMimeType(fsoUploadedFileDto.getFileMimeType());
        fsoUploadedFile.setFileName(fsoUploadedFileDto.getFileName());
        fsoUploadedFile.setFileSize(fsoUploadedFileDto.getFileSize());
        fsoUploadedFile.setFileUploadDateTime(new Date());
        fsoUploadedFile.setFileUploadedPath(fsoUploadedFileDto.getFileUploadedPath());
        fsoUploadedFile.setFileSubSystem(fsoUploadedFileDto.getFileSubSystem());
        fsoUploadedFile.setFileEntity(fsoUploadedFileDto.getFileEntity());
        fsoUploadedFile.setFileKind(fsoUploadedFileDto.getFileKind());
        fsoUploadedFileRepository.save(fsoUploadedFile);
        return fsoUploadedFileDto;
    }

    /**
     * این متد کلید فایل مورد نظر فایل آپلود شده را از ورودی دریافت کرده و مدل آن را خروجی میدهد
     *
     * @param fileKey کلید فایل آپلود شده مورد نظر
     * @return خروجی: مدل اطلاعات فایل آپلود شده
     * @throws IOException خطا
     */
    @Override
    public FsoUploadedFileDto readByFileKey(String fileKey) throws IOException {
        if ((fileKey == null) || (fileKey.length() == 0)) {
            throw new FsoException("null", BUSINESS_EXCEPTION_FSO_UPLOADED_INVALID_FILE_KEY + "::" + fileKey, "");
        }

        FsoUploadedFileDto fsoUploadedFileDto;
        FsoUploadedFile fsoUploadedFile = fsoUploadedFileRepository.findByFileKey(fileKey);

        if (fsoUploadedFile != null) {
            fsoUploadedFileDto = mapper.toDto(fsoUploadedFile);

            //خواندن اطلاعات فایل در مدل
            File uploadedFile = new File(fsoUploadedFileDto.getFileUploadedPath());
            try (FileInputStream fileInputStream = new FileInputStream(uploadedFile)) {
                byte[] fileContent = new byte[(int) uploadedFile.length()];
                if (!ObjectUtils.isEmpty(fileInputStream.read(fileContent))) {
                    fsoUploadedFileDto.setFileByteArray(fileContent);
                }

            }
        } else {
            throw new FsoException(fileKey, BUSINESS_EXCEPTION_FSO_UPLOADED_INVALID_FILE_KEY + "::" + fileKey, "");
        }

        return fsoUploadedFileDto;
    }

    /**
     * این متد کلید فایل آپلود شده مورد نظر را از ورودی دریافت کرده و آن را حذف مینماید
     *
     * @param fileKey کلید فایل آپلود شده مورد نظر
     */
    @Override
    public void delete(String fileKey) {
        FsoUploadedFile fsoUploadedFile = fsoUploadedFileRepository.findByFileKey(fileKey);
        if (fsoUploadedFile != null) {
            fsoUploadedFileRepository.delete(fsoUploadedFile);
            File file = new File(String.format("%s/%s", fsoPathUploadDirectory, fsoUploadedFile.getFileKey()));
            if ((file.exists())) {
                FileUtils.deleteQuietly(file);
            }
        } else {
            throw new FsoException(fileKey, BUSINESS_EXCEPTION_FSO_UPLOADED_INVALID_FILE_KEY + "::" + fileKey, "");
        }
    }


    /**
     * این متد مطابق مدل دیتابیسی اطلاعات فایل آپلود شده آن را در فایل سیستم ذخیره مینماید
     *
     * @param fsoUploadedFileDto مدل فایل آپلود شده
     * @param fileKey            کلید فایل آپلود شده
     * @return خروجی: مدل فایل آپلود شده کامل شده
     * @throws IOException خطا
     */
    private FsoUploadedFileDto saveUploadedFile(FsoUploadedFileDto fsoUploadedFileDto, String fileKey) throws IOException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String fileName = "uploaded_" + sdf.format(cal.getTime()) + "_" + fsoUploadedFileDto.getFileFullName();
        String fileKeyDirectoryPath = String.format("%s/%s", fsoPathUploadDirectory, fileKey);
        File directory = new File(fileKeyDirectoryPath);
        if (!directory.exists() && !directory.mkdir()) {
            throw new UtilityException(FsoTools.class, UtilityExceptionKeyEnum.FSO_DIRECTORY_CREATION_FAILED, fileKeyDirectoryPath);
        }
        FileUtils.writeByteArrayToFile(new File(String.format("%s/%s", fileKeyDirectoryPath, fileName)), fsoUploadedFileDto.getFileByteArray());
        fsoUploadedFileDto.setFileMimeType(FsoTools.getMimeTypeDto(fileKeyDirectoryPath + "/" + fileName).getMimeType());
        fsoUploadedFileDto.setFileUploadedPath(fileKeyDirectoryPath + "/" + fileName);
        return fsoUploadedFileDto;
    }


    /**
     * این متد کارکترهای غیر الفبایی را از رشته ورودی حذف میکند
     *
     * @param inputString رشته ورودی
     * @return خروجی: رشته بررسی شده و اصلاح شده
     */
    private static String removeNonAlphabetic(String inputString) {
        inputString = inputString.replaceAll("[^a-zA-Z]", "");
        return inputString;
    }

    /**
     * این متد انتهای مسیر دایرکتوری را چک میکند اگر اسلش ندارد به آن اضافه میکند
     *
     * @param directoryPath مسیر دایرکتوری
     * @return خروجی: مسیر دایرکتوری که حتما در انتهای آن اسلش دارد
     */
    private static String checkLastCharOfPath(String directoryPath) {
        String lastChar = directoryPath.substring(directoryPath.length() - 1);
        if (!"/".equals(lastChar)) {
            directoryPath += "/";
        }
        return directoryPath;
    }

    /**
     * این متد کارکترهای عربی را با معادل فارسی آن جایگزین میکند
     *
     * @param fileName نام فایل ورودی
     * @return خروجی: نام فایل اصلاح شده و بدون کارکترهای عربی
     */
    private String fixFailedFileNameCharacter(String fileName) {
        if (ObjectUtils.isEmpty(fileName)) {
            return "";
        }
        HashMap<String, String> replaceHashMap = new HashMap<>();
        replaceHashMap.put(Character.toString((char) 63), "ي");
        replaceHashMap.put(Character.toString((char) 1740), "ي");
        replaceHashMap.put(Character.toString((char) 1705), "ك");
        replaceHashMap.put(Character.toString((char) 1607), "ھ");
        replaceHashMap.put(Character.toString((char) 1575), "أ");
        replaceHashMap.put(Character.toString((char) 1570), "أ");
        replaceHashMap.put(Character.toString((char) 1608), "ؤ");
        for (Map.Entry<String, String> entry : replaceHashMap.entrySet()) {
            while (fileName.contains(entry.getKey())) {
                fileName = fileName.replace(entry.getKey(), entry.getValue());
            }
        }
        return fileName;
    }


    /**
     * متد بررسی وجود و دریافت تنظمیات آپلود فایل مطابق با نام زیرسیستم و انتیتی و نوع فایل
     *
     * @param fileSubSystem زیرسیستم انتیتی
     * @param fileEntity    انتیتی
     * @param fileKind      نوع فایل داخل انتیتی
     * @return خروجی: تنظیمات آپلود فایل
     */
    private FsoSettingDto getSetting(@NotNull SubSystemEnum fileSubSystem, @NotNull String fileEntity, @NotNull String fileKind) {
        //به دست آوردن تنظیمات از روی نام زیرسیستم و انتیتی و نوع فایل
        FsoSettingDto fsoSettingDto = null;
        String settingName = fileSubSystem.getValue() + "_" + fileEntity + "_" + fileKind;
        settingName = settingName.replace("-", "_");
        for (Field field : FsoSetting.class.getDeclaredFields()) {
            try {
                if (field.getType().equals(FsoSettingDto.class) && Modifier.isStatic(field.getModifiers()) && (settingName.equalsIgnoreCase(field.getName()))) {
                    fsoSettingDto = (FsoSettingDto) field.get(null);
                    break;
                }
            } catch (IllegalAccessException ignored) {

            }
        }

        //در صورتی که برای فایل ارسالی تنظیماتی انجام نشده باشد خطا صادر میکنیم
        if (ObjectUtils.isEmpty(fsoSettingDto)) {
            throw new FsoException(settingName, BUSINESS_EXCEPTION_FSO_UPLOADED_SETTING_NOT_FOUND, "");
        }
        return fsoSettingDto;
    }

}
