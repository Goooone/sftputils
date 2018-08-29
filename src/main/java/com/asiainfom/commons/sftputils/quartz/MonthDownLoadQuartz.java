package com.asiainfom.commons.sftputils.quartz;

import com.asiainfom.commons.sftputils.utils.DateUtils;
import com.asiainfom.commons.sftputils.utils.SftpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author king-pan
 * Date: 2018/8/24
 * Time: 下午3:35
 * Description: No Description
 */
@Slf4j
@Component
public class MonthDownLoadQuartz {

    @Autowired
    private SftpUtil sftpUtil;


    @Value("${sftp.localDirectory}")
    private String localDirectory;

    @Value("${sftp.rootDirectory}")
    private String rootDirectory;

    String fileA = "hubydcmcc_app_jihuo_";

    String fileB = "hubydcmcc_app_hechou_";

    String subfix = ".csv";

    @Scheduled(cron = "${quartz.month.cron}")
    public void timerToNow() {
        log.info("月文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp月文件下载开始");
        try {

            String ftpPath;
            String filePath = localDirectory + File.separator + "month";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                log.info("月文件下载-->创建新目录: {}", file.getAbsolutePath());
            }
            log.info("月文件下载-->文件存储路径: " + file.getAbsolutePath());
            String fileName1 = fileA + DateUtils.getBeforeMonthText() + subfix;
            File downLoadFile1 = new File(filePath + File.separator + fileName1);
            if (!downLoadFile1.exists()) {
                sftpUtil.login();
                ftpPath = rootDirectory + File.separator + "jihuo";
                sftpUtil.download(ftpPath, fileName1, downLoadFile1.getAbsolutePath());
                log.info("月文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile1.getAbsolutePath());
                sftpUtil.logout();
            } else {
                log.info("月文件下载-->文件{}已存在，不下载", downLoadFile1.getAbsolutePath());
            }
            String fileName2 = fileB + DateUtils.getBeforeMonthText() + subfix;
            File downLoadFile2 = new File(filePath + File.separator + fileName2);
            if (!downLoadFile2.exists()) {
                ftpPath = rootDirectory + File.separator + "hechou";

                sftpUtil.login();
                sftpUtil.download(ftpPath, fileName2, downLoadFile2.getAbsolutePath());
                log.info("月文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile2.getAbsolutePath());
            } else {
                log.info("月文件下载-->文件{}已存在，不下载", downLoadFile2.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("月文件下载-->sftp月文件下载失败:\n" + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            sftpUtil.logout();
        }
        log.info("月文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp月文件下载结束");
    }
}
