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


    @Value("${sftp.month.fileNames}")
    private String fileNames;


    @Value("${sftp.month.ftpPath}")
    private String ftpPath;

    @Value("${sftp.month.fileSubfix}")
    private String fileSubfix;


    @Scheduled(cron = "${quartz.month.cron}")
    public void timerToNow() {
        log.info("月文件下载-->当前时间: 【" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 】sftp月文件下载开始");
        try {

            if (ftpPath == null) {
                throw new RuntimeException("请配置application.properties中的sftp.month.ftpPath属性");
            }

            if (fileNames == null) {
                throw new RuntimeException("请配置application.properties中的sftp.month.fileNames属性");
            }

            String[] paths = ftpPath.split(",");

            String[] names = fileNames.split(",");

            String sftpPath;

            File downLoadFile;

            String filePath = localDirectory + File.separator + "month";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                log.info("月文件下载-->创建新目录: {}", file.getAbsolutePath());
            }
            sftpUtil.login();
            for (String path : paths) {
                for (String name : names) {
                    name = name + DateUtils.getBeforeMonthText() + fileSubfix;
                    downLoadFile = new File(filePath + File.separator + name);
                    if (!downLoadFile.exists()) {

                        sftpPath = rootDirectory + File.separator + path;
                        sftpUtil.download(sftpPath, name, downLoadFile.getAbsolutePath());
                        log.info("月文件下载-->从ftp:{}下载文件到-->{}", sftpUtil.getHost(), downLoadFile.getAbsolutePath());
                        sftpUtil.logout();
                    } else {
                        log.info("月文件下载-->文件{}已存在，不下载", downLoadFile.getAbsolutePath());
                    }
                }
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
