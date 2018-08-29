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
 * Time: 上午11:07
 * Description: No Description
 */
@Slf4j
@Component
public class DayDownLoadQuartz {


    @Autowired
    private SftpUtil sftpUtil;


    @Value("${sftp.localDirectory}")
    private String localDirectory;

    @Value("${sftp.rootDirectory}")
    private String rootDirectory;

    String fileA = "hubydcmcc_app_detail_";

    String fileB = "hubydcmcc_phone_detail_";

    String subfix = ".AVL";

    @Scheduled(cron = "${quartz.cron}")
    public void timerToNow() {
        log.info("日文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp下载开始");
        try {
            sftpUtil.login();
            String ftpPath = rootDirectory + File.separator + DateUtils.getMonthText();
            String filePath = localDirectory + File.separator + "day";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                log.info("日文件下载-->创建新目录: {}", file.getAbsolutePath());
            }
            log.info("日文件下载-->文件存储路径: " + file.getAbsolutePath());
            String fileName1 = fileA + DateUtils.getDayText() + subfix;
            String fileName2 = fileB + DateUtils.getDayText() + subfix;
            File downLoadFile1 = new File(filePath + File.separator + fileName1);
            if (!downLoadFile1.exists()) {
                sftpUtil.download(ftpPath, fileName1, downLoadFile1.getAbsolutePath());
                log.info("日文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile1.getAbsolutePath());
                sftpUtil.logout();
                sftpUtil.login();
            } else {
                log.info("日文件下载-->文件{}已存在，不下载", downLoadFile1.getAbsolutePath());
            }
            File downLoadFile2 = new File(filePath + File.separator + fileName2);
            if (!downLoadFile2.exists()) {
                sftpUtil.download(ftpPath, fileName2, downLoadFile2.getAbsolutePath());
                log.info("日文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile2.getAbsolutePath());
            } else {
                log.info("日文件下载-->文件{}已存在，不下载", downLoadFile2.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("日文件下载-->sftp下载失败:\n" + e.getMessage(), e);
            e.printStackTrace();
        } finally {
            sftpUtil.logout();
        }
        log.info("日文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp下载结束");
    }

}
