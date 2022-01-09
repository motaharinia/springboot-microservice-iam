package com.motaharinia.ms.iam.modules.fso.business.service;


import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.motaharinia.ms.iam.modules.fso.business.exception.FsoException;
import com.motaharinia.ms.iam.modules.fso.presentation.FsoUploadedFileDto;
import com.motaharinia.ms.iam.modules.fso.presentation.crudfilehandle.CrudFileHandleDto;
import com.motaharinia.ms.iam.modules.fso.presentation.validation.FsoSettingDto;
import com.motaharinia.msutility.custom.customfield.CustomDate;
import com.motaharinia.msutility.tools.encoding.EncodingTools;
import com.motaharinia.msutility.tools.fso.FsoConfigDto;
import com.motaharinia.msutility.tools.fso.FsoTools;
import com.motaharinia.msutility.tools.fso.check.FsoPathCheckDto;
import com.motaharinia.msutility.tools.fso.check.FsoPathCheckTypeEnum;
import com.motaharinia.msutility.tools.fso.content.FsoPathContentDto;
import com.motaharinia.msutility.tools.fso.download.FileDownloadDto;
import com.motaharinia.msutility.tools.fso.mimetype.FsoMimeTypeDto;
import com.motaharinia.msutility.tools.fso.mimetype.FsoMimeTypeEnum;
import com.motaharinia.msutility.tools.fso.upload.FileUploadedDto;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import com.motaharinia.msutility.tools.fso.view.FileViewDtoStatusEnum;
import com.motaharinia.msutility.tools.image.ImageTools;
import com.motaharinia.msutility.tools.zip.ZipTools;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس فایل سیستم
 */
@Service
public class FsoServiceImpl implements FsoService {


    /**
     * مسیر فایلهای ماژولهای پروزه
     */
    @Value("${fso.path.module:/api/v1.0/fso/module}")
    private String fsoPathModule;

    /**
     * تنظیمات فایل
     */
    private FsoConfigDto fsoConfigDto;

    private final Environment environment;

    /**
     * سرویس فایلهای آپلود شده
     */
    FsoUploadedFileService fsoUploadedFileService;


    /**
     * کلاس سازنده
     *
     * @param environment
     * @param fsoUploadedFileService سرویس فایلهای آپلود شده
     */
    public FsoServiceImpl(Environment environment, FsoUploadedFileService fsoUploadedFileService) {
        this.environment = environment;
        this.fsoUploadedFileService = fsoUploadedFileService;
        this.fsoConfigDto = new FsoConfigDto(new Integer[]{Integer.valueOf(environment.getRequiredProperty("fso.image.thumb.size.small")), Integer.valueOf(environment.getRequiredProperty("fso.image.thumb.size.large"))}, environment.getRequiredProperty("fso.image.thumb.extension"), Integer.valueOf(environment.getRequiredProperty("fso.directory.file.limit")));
    }

    private static final String BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_COUNT_VALIDATION = "BUSINESS_EXCEPTION.FSO_UPLOADED_FILE_COUNT_VALIDATION";
    private static final String BUSINESS_EXCEPTION_FSO_PATH_IS_NOT_FILE = "BUSINESS_EXCEPTION.FSO_PATH_IS_NOT_FILE";
    private static final String BUSINESS_EXCEPTION_LOG_UPLOADED_FILE_HANDLE_ENTITY_ID_IS_EMPTY = "BUSINESS_EXCEPTION.LOG_UPLOADED_FILE_HANDLE_ENTITY_ID_IS_EMPTY";
    private static final String BUSINESS_EXCEPTION_LOG_UPLOADED_FILE_HANDLE_ACTION_ENUM_INVALID = "BUSINESS_EXCEPTION.LOG_UPLOADED_FILE_HANDLE_ACTION_ENUM_INVALID";


    /**
     * این مند یک مسیر اصلی و فرعی را دریافت میکند و لیست مدل نمایش تمام فایلهای داخل آن را خروجی میدهد
     *
     * @param subSystemEntityEntityIdFileKindPath "/SHOP/member/120/profile-image" مسیر حاوی زیرسیستم و انتیتی و شناسه انتیتی و نوع فایل. مثال
     * @param entityIdFileKindPath                "/120/profile-image" مسیر حاوی شناسه انتیتی و نوع فایل. مثال
     * @return خروجی: لیست مدل نمایش تمام فایلهای داخل مسیر
     */
    @Override
    @NotNull
    public ArrayList<FileViewDto> fileViewDtoList(@NotNull String subSystemEntityEntityIdFileKindPath, @NotNull String entityIdFileKindPath) {
        ArrayList<FileViewDto> fileViewDtoList = new ArrayList<>();
        FileViewDto fileViewDto;
        String fullPath = fsoPathModule + subSystemEntityEntityIdFileKindPath;
        FsoPathContentDto fsoPathContentDto = FsoTools.pathContent(fullPath, new String[]{}, new String[]{}, new String[]{"Thumbs.db"}, new String[]{"thumb"}, false);
        for (int i = 0; i < fsoPathContentDto.getFileList().size(); i++) {
            fileViewDto = new FileViewDto();
            fileViewDto.setLastModifiedDate(new CustomDate(fsoPathContentDto.getFileList().get(i).getLastModifiedDate()));
            fileViewDto.setFullPath(entityIdFileKindPath + "/" + fsoPathContentDto.getFileList().get(i).getFullName());
            fileViewDto.setHashedPath(EncodingTools.base64Encode(fileViewDto.getFullPath()));
            fileViewDto.setFullName(fsoPathContentDto.getFileList().get(i).getFullName());
            fileViewDto.setName(fsoPathContentDto.getFileList().get(i).getName());
            fileViewDto.setExtension(fsoPathContentDto.getFileList().get(i).getExtension());
            fileViewDto.setMimeType(fsoPathContentDto.getFileList().get(i).getMimeType());
            Long size = fsoPathContentDto.getFileList().get(i).getSize();
            fileViewDto.setSize(size);
            fileViewDtoList.add(fileViewDto);
        }
        return fileViewDtoList;
    }


    /**
     * این متد لیستی از مسیرها شامل فایل و پوشه را از ورودی دریافت میکند و آنها را حذف میکند
     *
     * @param pathList لیست مسیرها برای حذف
     */
    @Override
    public void delete(@NotNull List<String> pathList) {
        FsoMimeTypeDto fsoMimeTypeDto;
        for (String pathFile : pathList) {
            FsoPathCheckDto fsoPathCheckDto;
            try {
                fsoPathCheckDto = FsoTools.pathExistCheck(fsoPathModule + pathFile);
            } catch (Exception exception) {
                fsoPathCheckDto = null;
            }
            if (!ObjectUtils.isEmpty(fsoPathCheckDto)) {
                if (fsoPathCheckDto.getTypeEnum().equals(FsoPathCheckTypeEnum.FILE)) {
                    fsoMimeTypeDto = FsoTools.getMimeTypeDto(fsoPathModule + pathFile);
                    FsoTools.delete(fsoPathModule + pathFile, fsoMimeTypeDto.getType().equals(FsoMimeTypeEnum.IMAGE), fsoConfigDto);
                } else {
                    FsoTools.delete(fsoPathModule + pathFile, false, fsoConfigDto);
                }
            }
        }
    }


    /**
     * این متد یک مسیر را از ورودی دریافت میکند و آن را در صورت عدم وجود می سازد
     *
     * @param directoryPath مسیر جهت ایجاد
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public void createDirectory(@NotNull String directoryPath) throws IOException {
        FsoTools.createDirectoryIfNotExist(fsoPathModule + directoryPath);
    }


    /**
     * این متد مسیر مبدا و مسیر مقصد و یک سوال که آیا مسیر ورودی تصویر بندانگشتی دارد یا خیر و یک سوال که آیا مسیر مقصد در صورت عدم وجود ایجاد شود و یک سوال که در صورت وجود مسیر در مقصد یک نام جدید با -copy بسازد را از ورودی دریافت میکند و مسیر مبدا را در مسیر مقصد کپی میکند <br>
     * اگر مسیر مقصد از قبل وجود داشته باشد مانند ویندوز نام مقصد را غیرتکراری میکند و کپی را انجام میدهد
     *
     * @param pathFrom              مسیر مبدا که میتواند دایرکتوری یا فایل باشد
     * @param pathTo                مسیر مقصد که اگر مسیر مبدا فایل بوده باید این مسیر نیز مسیر کامل فایل باشد
     * @param withThumbnail         مسیر مبدا حاوی تصویر بندانگشتی
     * @param withDirectoryCreation در صورت عدم وجود مسیر مقصد آن را ایجاد کند؟
     * @param withRenameOnExist     در صورت وجود مسیر در مقصد یک نام جدید با -copy بسازد
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public void copy(@NotNull String pathFrom, @NotNull String pathTo, @NotNull Boolean withThumbnail, @NotNull Boolean withDirectoryCreation, @NotNull Boolean withRenameOnExist) throws IOException {
        pathTo = fsoPathModule + pathTo;
        pathFrom = fsoPathModule + pathFrom;
        FsoTools.copy(pathFrom, pathTo, withThumbnail, fsoConfigDto, withDirectoryCreation, withRenameOnExist);
    }


    /**
     * این متد یک مسیر مبدا و یک مسیر مقصد و یک سوال که آیا مسیر مبدا تصویر بندانگشتی دارد یا خیر و و یک سوال که در صورت وجود نداشتن مسیر آن را بسازد یا خیر از ورودی دریافت میکند و مسیر مبدا را به مسیر مقصد انتقال/تغییرنام میدهد
     *
     * @param pathFrom              مسیر مبدا
     * @param pathTo                مسیر مقصد
     * @param withThumbnail         مسیر مبدا حاوی تصویر بندانگشتی
     * @param withDirectoryCreation در صورت عدم وجود مسیر مقصد آن را ایجاد کند؟
     */
    @Override
    public void move(@NotNull String pathFrom, @NotNull String pathTo, @NotNull Boolean withThumbnail, @NotNull Boolean withDirectoryCreation) {
        pathTo = fsoPathModule + pathTo;
        pathFrom = fsoPathModule + pathFrom;
        FsoTools.move(pathFrom, pathTo, withThumbnail, fsoConfigDto, withDirectoryCreation);
    }


    /**
     * این متد یک مسیر حاوی زیرسیستم و انتیتی و یک مسیر هش شده شناسه انتیتی تا فایل و اندازه تصویر بندانگشتی از ورودی دریافت میکند و مدل دانلود فایل را خروجی میدهد
     *
     * @param subSystemAndEntityPath مسیر حاوی زیرسیستم و انتیتی
     * @param hashedPath             مسیر هش شده شناسه انتیتی تا فایل
     * @param thumbSize              ابعاد تصویر بندانگشتی
     * @return خروجی: مدل دانلود فایل
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    @NotNull
    public FileDownloadDto download(@NotNull String subSystemAndEntityPath, @NotNull String hashedPath, String thumbSize) throws IOException {
        String fileFullPath = fsoPathModule + subSystemAndEntityPath + EncodingTools.base64decode(hashedPath);

        //اگر درخواست تصویر بندانگشتی شده بود آن را به جای فایل اصلی در مسیر قرار میدهیم
        if (!ObjectUtils.isEmpty(thumbSize)) {
            fileFullPath = fileFullPath + "-" + thumbSize + "." + this.fsoConfigDto.getThumbExtension();
        }
        FsoPathCheckDto fsoPathCheckDto = FsoTools.pathExistCheck(fileFullPath);
        if (!fsoPathCheckDto.getTypeEnum().equals(FsoPathCheckTypeEnum.FILE)) {
            throw new FsoException(hashedPath, BUSINESS_EXCEPTION_FSO_PATH_IS_NOT_FILE + "::" + hashedPath, "");
        }

        //تکمیل اطلاعات مدل دانلود
        FileDownloadDto fileDownloadDto = new FileDownloadDto();
        Path path = Paths.get(fileFullPath);
        byte[] dataByteArray = FsoTools.downloadPathAndRead(fileFullPath);
        fileDownloadDto.setDataByteArray(dataByteArray);
        fileDownloadDto.setSize((long) dataByteArray.length);
        fileDownloadDto.setMimeType(Files.probeContentType(path));
        fileDownloadDto.setFullName(path.getFileName().toString());
        fileDownloadDto.setName(FsoTools.getFileNameWithoutExtension(fileDownloadDto.getFullName()));
        fileDownloadDto.setExtension(FsoTools.getFileExtension(fileDownloadDto.getFullName()));
        return fileDownloadDto;
    }


    /**
     * این متد یک مسیر حاوی زیرسیستم و انتیتی و یک مسیر هش شده شناسه انتیتی تا فایل و اندازه تصویر بندانگشتی از ورودی دریافت میکند و مدل دانلود فایل را خروجی میدهد
     *
     * @param subSystemAndEntityPath "/SHOP/member" مسیر حاوی زیرسیستم و انتیتی و شناسه انتیتی و نوع فایل. مثال
     * @param entityIdFileKindPath   "/120/profile-image" مسیر حاوی شناسه انتیتی و نوع فایل. مثال
     * @param thumbSize              ابعاد تصویر بندانگشتی
     * @return خروجی: مدل دانلود فایل
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    @NotNull
    public FileDownloadDto downloadSingle(@NotNull String subSystemAndEntityPath, @NotNull String entityIdFileKindPath, String thumbSize) throws IOException {
        String fileFullPath = fsoPathModule + subSystemAndEntityPath + "/" + entityIdFileKindPath;

        //به دست آوردن اولین فایل موجود در مسیر داده شده
        FsoPathContentDto fsoPathContentDto = FsoTools.pathContent(fileFullPath, new String[]{}, new String[]{}, new String[]{"Thumbs.db"}, new String[]{"thumb"}, false);
        if (!ObjectUtils.isEmpty(fsoPathContentDto.getFileList())) {
            fileFullPath = fileFullPath + fsoPathContentDto.getFileList().get(0).getFullName();
            //اگر درخواست تصویر بندانگشتی شده بود آن را به جای فایل اصلی در مسیر قرار میدهیم
            if (!ObjectUtils.isEmpty(thumbSize)) {
                fileFullPath = fileFullPath + "-" + thumbSize + "." + this.fsoConfigDto.getThumbExtension();
            }
        }

        //بررسی وجود فایل
        FsoTools.pathExistCheck(fileFullPath);

        //تکمیل اطلاعات مدل دانلود
        FileDownloadDto fileDownloadDto = new FileDownloadDto();
        Path path = Paths.get(fileFullPath);
        byte[] dataByteArray = FsoTools.downloadPathAndRead(fileFullPath);
        fileDownloadDto.setDataByteArray(dataByteArray);
        fileDownloadDto.setSize((long) dataByteArray.length);
        fileDownloadDto.setMimeType(Files.probeContentType(path));
        fileDownloadDto.setFullName(path.getFileName().toString());
        fileDownloadDto.setName(FsoTools.getFileNameWithoutExtension(fileDownloadDto.getFullName()));
        fileDownloadDto.setExtension(FsoTools.getFileExtension(fileDownloadDto.getFullName()));
        return fileDownloadDto;
    }

    /**
     * این متد یک مدل فایل آپلود شده در مسیر آپلود را از ورودی دریافت میکند و بعد از انتقال آن در مسیر ماژول نظر ، مسیر رمزگذاری شده فایل جابجا شده را خروجی میدهد
     *
     * @param fileUploadedDto مدل فایل آپلود شده
     * @return خروجی: مسیر رمزگذاری شده فایل ثبت شده
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    @NotNull
    public String uploadedFileHandleToModule(@NotNull FileUploadedDto fileUploadedDto) throws IOException {
        FsoMimeTypeDto fsoMimeTypeDto = FsoTools.getMimeTypeDto(fsoPathModule + fileUploadedDto.getDirectoryRealPath() + "/" + fileUploadedDto.getFullName());
        boolean withThumbnail = false;
        if (fsoMimeTypeDto.getType().equals(FsoMimeTypeEnum.IMAGE)) {
            withThumbnail = true;
        }
        return FsoTools.uploadWriteToPath(fsoPathModule + fileUploadedDto.getDirectoryRealPath() + "/", fileUploadedDto.getFullName(), fileUploadedDto.getDataByteArray(), withThumbnail, fsoConfigDto);
    }

    /**
     * این متد آرایه بایت داده فایل زیپ و مسیری برای استخراج فایل زیپ و رمز استخراج فایل زیپ را از ورودی دریافت میکند و در مسیر آن را استخراج مینماید
     *
     * @param sourceByteArray              آرایه بایت داده فایل زیپ
     * @param destinationDirectoryForUnzip مسیری برای استخراج فایل زیپ
     * @param password                     رمز استخراج فایل زیپ
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public void unzipFromByteArray(byte[] sourceByteArray, @NotNull String destinationDirectoryForUnzip, String password) throws IOException {
        ZipTools.unzipFromByteArray(sourceByteArray, fsoPathModule + destinationDirectoryForUnzip, password);
    }

    /**
     * این متد مسیر فایل زیپ و مسیری برای استخراج فایل زیپ و رمز استخراج فایل زیپ را از ورودی دریافت میکند و در مسیر آن را استخراج مینماید
     *
     * @param sourceZipFilePath            مسیر فایل زیپ
     * @param destinationDirectoryForUnzip مسیری برای استخراج فایل زیپ
     * @param password                     رمز استخراج فایل زیپ
     * @throws ZipException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public void unzip(@NotNull String sourceZipFilePath, @NotNull String destinationDirectoryForUnzip, String password) throws ZipException {
        ZipTools.unzip(fsoPathModule + sourceZipFilePath, fsoPathModule + destinationDirectoryForUnzip, password);
    }


    /**
     * این متد یک مسیر کامل فایل یا دایرکتوری ورودی دریافت میکند و چک میکند آن مسیر وجود داشته باشد و مدل حاوی نوع مسیر (فایل یا دایرکتوری) و مرجع فایل را خروجی میدهد
     *
     * @param path مسیر کامل فایل یا دایرکتوری ورودی
     * @return خروجی: مدل حاوی نوع مسیر (فایل یا دایرکتوری) و مرجع فایل
     */
    @Override
    @NotNull
    public FsoPathCheckDto pathExistCheck(@NotNull String path) {
        return FsoTools.pathExistCheck(fsoPathModule + path);
    }


    /**
     * این متد یک مسیر فایل از ورودی دریافت میکند و آرایه بایت داده داخل آن را خروجی میدهد
     *
     * @param realPath مسیر فایل
     * @return خروجی:  آرایه بایت داده داخل آن
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public byte[] readFile(@NotNull String realPath) throws IOException {
        File file = new File(fsoPathModule + realPath);
        return readFromFile(file);
    }

    /**
     * این متد یک شیی فایل از ورودی دریافت میکند و آرایه بایت داده داخل آن را خروجی میدهد
     *
     * @param file شیی فایل
     * @return خروجی:  آرایه بایت داده داخل آن
     * @throws IOException این متد ممکن است اکسپشن داشته باشد
     */
    @Override
    public byte[] readFromFile(@NotNull File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    /**
     * این متد طبق شرایط ورودی لیست مدلهای کامل اطلاعات فایلهای یک ماژول را خروجی میدهد
     *
     * @param fsoSettingDto مدل تنظیمات فایل
     * @param entityId      شناسه انتیتی مورد نظر که فایلهای آن را میخواهیم
     * @return خروجی: لیستی از مدل مشاهده فایل
     */
    @Override
    @NotNull
    public ArrayList<FileViewDto> readFileViewDtoList(@NotNull FsoSettingDto fsoSettingDto, @NotNull Long entityId) {
        //example: "/SHOP/member/120/profile-image"
        String entityKindDirectoryPath = fsoSettingDto.getEntityKindDirectoryPath(entityId);
        //example: "/120/profile-image"
        String kindDirectoryPath = fsoSettingDto.getKindDirectoryPath(entityId);
        FsoPathCheckDto fsoPathCheckDto;
        try {
            fsoPathCheckDto = FsoTools.pathExistCheck(fsoPathModule + entityKindDirectoryPath);
        } catch (Exception exception) {
            fsoPathCheckDto = null;
        }
        if ((!ObjectUtils.isEmpty(fsoPathCheckDto)) && (fsoPathCheckDto.getTypeEnum().equals(FsoPathCheckTypeEnum.DIRECTORY))) {
            return this.fileViewDtoList(entityKindDirectoryPath, kindDirectoryPath);
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * این متد یک مدل کراد فایل را میگیرد و فایل را نسبت به فعل ثبت یا ویرایش یا حذف در فایل سیستم سامانه مدیریت میکند
     *
     * @param crudFileHandleDto مدل کراد فایل
     * @throws IOException خطا
     */
    @Override
    public void crudHandle(@NotNull CrudFileHandleDto crudFileHandleDto) throws IOException, ImageProcessingException, MetadataException {

        //متد اعتبارسنجی فایلهای ارسالی (قبلا در آپلود فایل پسوند و سایز فایلها چک شده است)
        this.crudHandleValidate(crudFileHandleDto);

        //نوع عملیات ثبت / ویرایش / حذف
        switch (crudFileHandleDto.getCrudFileHandleActionEnum()) {
            case ENTITY_CREATE:
                //در صورتی که فایلی برای اضافه شدن در ثبت وجود دارد پوشه انتیتی آن را حذف میکنیم
                //ex: /SHOP/member/120
                this.delete(List.of(crudFileHandleDto.getFsoSettingDto().getEntityDirectoryPath() + "/" + crudFileHandleDto.getEntityId().toString()));
                //آپلود فایلهای جدید اضافه شده
                for (FileViewDto fileViewDto : crudFileHandleDto.getFileViewDtoList()) {
                    crudHandleAdded(fileViewDto, crudFileHandleDto.getFsoSettingDto(), crudFileHandleDto.getEntityId());
                }
                break;

            case ENTITY_UPDATE:
                //مدیریت فایلهای ویرایش شده در کلاینت
                for (FileViewDto fileViewDto : crudFileHandleDto.getFileViewDtoList()) {
                    switch (fileViewDto.getStatusEnum()) {
                        //اگر فایلی در کلاینت اضافه شده است
                        case ADDED:
                            crudHandleAdded(fileViewDto, crudFileHandleDto.getFsoSettingDto(), crudFileHandleDto.getEntityId());
                            break;
                        //اگر فایلی در کلاینت حذف شده است
                        case DELETED:
                            //ex: /SHOP/member/120/2021-10-20_19-56-05_2.png
                            this.delete(List.of(crudFileHandleDto.getFsoSettingDto().getEntityKindDirectoryPath(crudFileHandleDto.getEntityId()) + "/" + fileViewDto.getFullName()));
                            break;
                        //اگر فایلی در کلاینت تغییری نداشته است
                        default:
                            break;
                    }
                }
                break;

            case ENTITY_DELETE:
                // پوشه انتیتی را حذف میکنیم
                //ex: /SHOP/member/120
                this.delete(List.of(crudFileHandleDto.getFsoSettingDto().getEntityDirectoryPath() + "/" + crudFileHandleDto.getEntityId().toString()));
                break;
            default:
                throw new FsoException(crudFileHandleDto.getCrudFileHandleActionEnum().getValue(), BUSINESS_EXCEPTION_LOG_UPLOADED_FILE_HANDLE_ACTION_ENUM_INVALID + "::" + crudFileHandleDto.getCrudFileHandleActionEnum().getValue(), "");

        }
    }


    /**
     * متد فایلهای جدید که باید از پوشه آپلود شده در پوشه ماژول منتقل شوند و از دیتابیس فایلهای آپلود شده اطلاعات آنها حذف گردد
     *
     * @param fileViewDto   مدل مشاهده فایل
     * @param fsoSettingDto مدل تنظیمات فایل
     * @param entityId      شناسه انتیتی
     * @throws IOException              خطا
     * @throws ImageProcessingException خطا
     * @throws MetadataException        خطا
     */
    private void crudHandleAdded(FileViewDto fileViewDto, FsoSettingDto fsoSettingDto, Long entityId) throws IOException, ImageProcessingException, MetadataException {
        //تنظیم رشته تاریخ-زمان پیشوند نام فایلها در پوشه نوع فایل که اگر فایل با نام تکراری آپلود شد در پوشه نوع فایل با پیشوند یونیک شود
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        String dateTimePrefix = simpleDateFormat.format(calendar.getTime());


        //خواندن اطلاعات فایل آپلود شده از دیتابیس فایلهای آپلود شده
        FsoUploadedFileDto fsoUploadedFileDto = fsoUploadedFileService.readByFileKey(fileViewDto.getKey());
        //به دست آوردن مسیر پوشه نوع فایل مورد نظر در انتیتی مورد نظر
        ///SHOP/member/120/profile-image/
        String kindDirectoryPath = fsoSettingDto.getEntityKindDirectoryPath(entityId);
        //بررسی و در صورت نیاز ساخت مسیر پوشه نوع فایل مورد نظر انتیتی
        FsoTools.pathDirectoryPrepare(fsoPathModule + kindDirectoryPath);
        //تبدیل مدل فایل آپلود به مدل آپلود جهت آپلود
        FileUploadedDto fileUploadedDto = fsoUploadedFileDto.getFileUploadedDto();
        fileUploadedDto.setDirectoryRealPath(kindDirectoryPath);
        //بررسی نیاز به تغییر اندازه داشتن فایلهای تصویری
        if ((fsoSettingDto.getHeight() != null) && (fsoSettingDto.getWidth() != null) && (fsoSettingDto.getHeight() > 0) && (fsoSettingDto.getWidth() > 0)) {
            //اگر فایل مورد نظر تصویر باشد و در ورودی خواسته شده باشد که آن فایل تغییر اندازه بشود
            byte[] resizedFileByteArray = ImageTools.imageResize(fileUploadedDto.getDataByteArray(), fileUploadedDto.getExtension(), fsoSettingDto.getWidth(), fsoSettingDto.getHeight());
            fileUploadedDto.setDataByteArray(resizedFileByteArray);
            fileUploadedDto.setSize((long) fileUploadedDto.getDataByteArray().length);
        } else {
            //اگر فایل مورد نظر تصویر نباشد و یا نیاز به تغییر اندازه نداشته باشد
            fileUploadedDto.setName(dateTimePrefix + "_" + fileUploadedDto.getName());
            fileUploadedDto.setFullName(dateTimePrefix + "_" + fileUploadedDto.getFullName());
        }
        this.uploadedFileHandleToModule(fileUploadedDto);
        //حذف اطلاعات فایل آپلود شده از دیتابیس فایلهای آپلود شده
        fsoUploadedFileService.delete(fileViewDto.getKey());
    }

    /**
     * متد اعتبارسنجی فایلهای ارسالی
     *
     * @param crudFileHandleDto مدل کراد فایل
     */
    private void crudHandleValidate(@NotNull CrudFileHandleDto crudFileHandleDto) {
        //اعتبارسنجی وجود شناسه انتیتی
        if (ObjectUtils.isEmpty(crudFileHandleDto.getEntityId())) {
            throw new FsoException("crudFileHandleDto.getEntityId():null", BUSINESS_EXCEPTION_LOG_UPLOADED_FILE_HANDLE_ENTITY_ID_IS_EMPTY, "");
        }

        //اعتبار سنجی تعداد فایل
        Long countAddedOrExisted = crudFileHandleDto.getFileViewDtoList().stream().filter(item -> item.getStatusEnum().equals(FileViewDtoStatusEnum.ADDED) || item.getStatusEnum().equals(FileViewDtoStatusEnum.EXISTED)).count();
        Long countDeleted = crudFileHandleDto.getFileViewDtoList().stream().filter(item -> item.getStatusEnum().equals(FileViewDtoStatusEnum.DELETED)).count();
        if (countAddedOrExisted - countDeleted > crudFileHandleDto.getFsoSettingDto().getCount()) {
            throw new FsoException(crudFileHandleDto.getEntityId().toString(), BUSINESS_EXCEPTION_FSO_UPLOADED_FILE_COUNT_VALIDATION, "");
        }
    }
}
