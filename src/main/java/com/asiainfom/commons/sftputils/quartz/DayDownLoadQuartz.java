package com.asiainfom.commons.sftputils.quartz;

import com.asiainfom.commons.sftputils.utils.Constants;
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

    @Value("${sftp.day.fileSubfix}")
    private String subfix;


    @Value("${sftp.day.fileNames}")
    private String fileNames;


    @Value("${sftp.day.ftpPath}")
    private String ftpPath;


    @Scheduled(cron = "${quartz.cron}")
    public void timerToNow() {
        log.info("日文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp下载开始");
        try {
            sftpUtil.login();
            if (ftpPath.indexOf(Constants.DAY_$MONTH) > -1) {
                ftpPath = rootDirectory + File.separator + DateUtils.getMonthText();
            } else {
                ftpPath = rootDirectory + File.separator + "day";
            }
            String filePath = localDirectory + File.separator + "day";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                log.info("日文件下载-->创建新目录: {}", file.getAbsolutePath());
            }
            String[] names = fileNames.split(",");
            if (names != null && names.length > 0) {
                File downLoadFile;
                for (String name : names) {
                    name += DateUtils.getDayText() + subfix;
                    downLoadFile = new File(filePath + File.separator + name);
                    if (!downLoadFile.exists()) {
                        log.info("日文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile.getAbsolutePath());
                        sftpUtil.download(ftpPath, name, downLoadFile.getAbsolutePath());
                    } else {
                        log.info("日文件下载-->文件{}已存在，不下载", downLoadFile.getAbsolutePath());
                    }
                }
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
