package com.motaharinia.ms.iam.modules.fso.presentation;

import com.motaharinia.ms.iam.modules.fso.business.enumeration.FsoThumbSizeEnum;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoService;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoUploadedFileService;
import com.motaharinia.ms.iam.modules.fso.presentation.frontuploader.FineUploaderChunkDto;
import com.motaharinia.ms.iam.modules.fso.presentation.frontuploader.FineUploaderResponseDto;
import com.motaharinia.msutility.tools.fso.download.FileDownloadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس کنترلر فایل
 */
@RestController
@RequestMapping({"/api/v1.0/fso"})
public class FsoController {

    private final FsoUploadedFileService fsoUploadedFileService;
    private final FsoService fsoService;

    public FsoController(FsoUploadedFileService fsoUploadedFileService, FsoService fsoService) {
        this.fsoUploadedFileService = fsoUploadedFileService;
        this.fsoService = fsoService;
    }

    /**
     * این متد با دریافت تکه تکه داده فایل از کلاینت بک پنل فایل را آپلود و در سرور ذخیره مینماید
     *
     * @param fileSubSystem    زیرسیستم انتیتی
     * @param fileEntity       انتیتی
     * @param fileKind         نوع فایل داخل انتیتی
     * @param multipartFile    تکه داده در حال آپلود فایل
     * @param fileKey          کلید فایلی که در کلاینت تولید میشود و برای هر فایل در حال آپلود یونیک است
     * @param fileFullName     نام و پسوند فایل در کلاینت
     * @param fileChunkCount   تعداد تکه های داده فایل
     * @param fileChunkIndex   شماره تکه فعلی از داده فایل در حال ارسال
     * @param fileChunkSize    حجم هر تکه داده فایل جهت آپلود
     * @param fileSize         حجم کل فایل
     * @param qqpartbyteoffset آفست داده
     * @return خروجی: در صورت عدم وجود خطا همیشه مقدار ترو خروجی میدهد
     * @throws IOException خطا
     */
    @PostMapping(value = "/upload/{subSystem}/{entity}/{kind}/fine")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody
    FineUploaderResponseDto upload(
            @PathVariable("subSystem") SubSystemEnum fileSubSystem,
            @PathVariable("entity") String fileEntity,
            @PathVariable("kind") String fileKind,
            @RequestParam(name = "qqfile") MultipartFile multipartFile,
            @RequestParam(name = "qquuid") String fileKey,
            @RequestParam(name = "qqfilename") String fileFullName,
            @RequestParam(name = "qqtotalparts", defaultValue = "1") Integer fileChunkCount,
            @RequestParam(name = "qqpartindex", defaultValue = "0") Integer fileChunkIndex,
            @RequestParam(name = "qqchunksize", required = false, defaultValue = "0") Long fileChunkSize,
            @RequestParam(name = "qqtotalfilesize", required = false) Long fileSize,
            @RequestParam(name = "qqpartbyteoffset", required = false, defaultValue = "0") Integer qqpartbyteoffset
    ) throws IOException {
        FineUploaderChunkDto fineUploaderChunkDto = new FineUploaderChunkDto();
        fineUploaderChunkDto.setFileKey(fileKey);
        fineUploaderChunkDto.setFileFullName(fileFullName);
        fineUploaderChunkDto.setFileSize(fileSize);
        fineUploaderChunkDto.setFileSubSystem(fileSubSystem);
        fineUploaderChunkDto.setFileEntity(fileEntity);
        fineUploaderChunkDto.setFileKind(fileKind);
        fineUploaderChunkDto.setFileChunkCount(fileChunkCount);
        fineUploaderChunkDto.setFileChunkIndex(fileChunkIndex);
        fineUploaderChunkDto.setFileChunkSize(fileChunkSize);
        fineUploaderChunkDto.setFileByteArray(multipartFile.getBytes());
        //ارسال اطلاعات قسمتی از اپلود برای سرویس آپلود فایل
        fsoUploadedFileService.uploadToFileDto(fineUploaderChunkDto);
        //ایجاد خروجی جهت آپلود فایل در فرانت پنل
        FineUploaderResponseDto fineUploaderResponseDto = new FineUploaderResponseDto();
        fineUploaderResponseDto.setSuccess(Boolean.TRUE);
        return fineUploaderResponseDto;
    }


    /**
     * این متد بر اساس زیرسیستم و انتیتی و مسیر هش شده و اینکه آیا برای مشاهده یا دانلود و با چه اندازه ای نیاز است داده فایل را دانلود و در اختیار کلاینت قرار میدهد
     *
     * @param response    شیی پاسخ به کلاینت
     * @param subSystem   زیرسیستم انتیتی
     * @param entity      انتیتی
     * @param hashedPath  مسیر هش شده فایل
     * @param forDownload آیا پنجره دانلود نمایش داده شود؟
     * @param thumbSize   اندازه تصویر بندانگشتی
     * @throws IOException خطا
     */
    @GetMapping(value = "/download/{subSystem}/{entity}/{hashedPath}/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public void download(HttpServletResponse response,
                         @PathVariable("subSystem") SubSystemEnum subSystem,
                         @PathVariable("entity") String entity,
                         @PathVariable("hashedPath") String hashedPath,
                         @RequestParam(required = false, defaultValue = "false") boolean forDownload,
                         @RequestParam(required = false, defaultValue = "") FsoThumbSizeEnum thumbSize) throws IOException {
        String mainPath = String.format("/%s/%s", removeNonAlphabetic(subSystem.getValue()), removeNonAlphabetic(entity));
        String thumbSizeString = null;
        if (thumbSize != null) {
            thumbSizeString = thumbSize.getValue().toLowerCase();
        }
        //FileDownloadDto fileDownloadDto = fsoService.download(mainPath.toLowerCase(), hashedPath, thumbSizeString);
        FileDownloadDto fileDownloadDto = fsoService.download(mainPath, hashedPath, thumbSizeString);
        //تنظیمات هدر برای ارسال به کلاینت
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setDateHeader("Max-Age", 0);
        response.setContentType(fileDownloadDto.getMimeType());
        response.setContentLength(fileDownloadDto.getSize().intValue());

        String fileName = URLEncoder.encode(fileDownloadDto.getFullName(), StandardCharsets.UTF_8);
        fileName = URLDecoder.decode(fileName, "ISO8859_1");

        //اگر درخواست شده پنجره دانلود برای کلاینت باز شود
        String contentDispositionValue = "inline";
        if (forDownload) {
            contentDispositionValue = "attachment";
        }
        response.setHeader("Content-Disposition", String.format("%s; filename=\"%s\"", contentDispositionValue, fileName));
        //نوشتن داده فایل بر روی شیی پاسخ به کلاینت
        OutputStream outputStream = response.getOutputStream();
        FileCopyUtils.copy(fileDownloadDto.getDataByteArray(), outputStream);
        outputStream.close();
        outputStream.flush();
    }

    /**
     * این متد بر اساس زیرسیستم و نوع تصویر مورد نیاز در انتیتی با شناسه مورد نظر و اینکه آیا برای مشاهده یا دانلود و با چه اندازه ای نیاز است داده فایل را دانلود و در اختیار کلاینت قرار میدهد<br>
     * این متد فقط برای نوع فایلهایی در انتیتی ها استفاده میشود که از ابتدا قرار بوده تک فایل باشند مثل تصویر پروفایل یک کاربر
     *
     * @param response       شیی پاسخ به کلاینت
     * @param subSystem      زیرسیستم انتیتی
     * @param entity         انتیتی
     * @param entityId       شناسه انتیتی
     * @param fileKindFolder نوع فایل انتیتی
     * @param forDownload    آیا پنجره دانلود نمایش داده شود؟
     * @param thumbSize      اندازه تصویر بندانگشتی
     * @throws IOException خطا
     */
    @GetMapping(value = "/download/single/{subSystem}/{entity}/{entityId}/{fileKindFolder}/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public void downloadSingle(HttpServletResponse response,
                               @PathVariable("subSystem") SubSystemEnum subSystem,
                               @PathVariable("entity") String entity,
                               @PathVariable("entityId") Long entityId,
                               @PathVariable("fileKindFolder") String fileKindFolder,
                               @RequestParam(required = false, defaultValue = "false") boolean forDownload,
                               @RequestParam(required = false, defaultValue = "") FsoThumbSizeEnum thumbSize) throws IOException {
        String mainPath = String.format("/%s/%s", removeNonAlphabetic(subSystem.getValue()), removeNonAlphabetic(entity));
        String singleDirectoryPath = String.format("/%s/%s/", entityId.toString(), fileKindFolder);
        String thumbSizeString = null;
        if (thumbSize != null) {
            thumbSizeString = thumbSize.getValue().toLowerCase();
        }
        FileDownloadDto fileDownloadDto = fsoService.downloadSingle(mainPath.toLowerCase(), singleDirectoryPath.toLowerCase(), thumbSizeString);
        //تنظیمات هدر برای ارسال به کلاینت
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setDateHeader("Max-Age", 0);
        response.setContentType(fileDownloadDto.getMimeType());
        response.setContentLength(fileDownloadDto.getSize().intValue());

        //اگر درخواست شده پنجره دانلود برای کلاینت باز شود
        String contentDispositionValue = "inline";
        if (forDownload) {
            contentDispositionValue = "attachment";
        }
        response.setHeader("Content-Disposition", String.format("%s; filename=\"%s\"", contentDispositionValue, fileDownloadDto.getFullName()));
        //نوشتن داده فایل بر روی شیی پاسخ به کلاینت
        OutputStream outputStream = response.getOutputStream();
        FileCopyUtils.copy(fileDownloadDto.getDataByteArray(), outputStream);
        outputStream.close();
        outputStream.flush();
    }


    /**
     * این متد یک کلید فایل آپلود شده را از ورودی دریافت کرده و آن را از مسیر فایلهای آپلود شده حذف میکند
     *
     * @param fileKey کلید فایل
     */
    @DeleteMapping(value = "/deleteUploadedFile/{fileKey}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public void deleteUploadedFile(@PathVariable("fileKey") String fileKey) {
        fsoUploadedFileService.delete(fileKey);
    }

    /**
     * این متد یک رشته از ورودی دریافت میکند و کارکترهای غیرالفبایی انگلیسی آن را حذف کرده و خروجی میدهد
     *
     * @param input رشته ورودی
     * @return خروجی: رشته ای که فقط حروف انگلیسی دارد
     */
    private String removeNonAlphabetic(String input) {
        input = input.replaceAll("[^a-zA-Z_]", "");
        return input;
    }


}
