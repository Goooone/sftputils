package com.asiainfom.commons.sftputils.quartz;

import com.asiainfom.commons.sftputils.utils.SftpUtil;
import com.jcraft.jsch.ChannelSftp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 *
 * @author king-pan
 * Date: 2018/8/24
 * Time: 下午4:11
 * Description: No Description
 */
@Slf4j
@Component
public class AllFilterDownLoad {


    @Autowired
    private SftpUtil sftpUtil;


    @Value("${sftp.localDirectory}")
    private String localDirectory;

    @Value("${sftp.rootDirectory}")
    private String rootDirectory;

    String fileA = "hubydcmcc_app_detail_";

    String fileB = "hubydcmcc_phone_detail_";

    String subfix = ".AVL";

    @Value("${sftp.allDownload}")
    private boolean flag;

    @Scheduled(cron = "${quartz.cron}")
    public void timerToNow() {
        if (flag) {
            log.info("开启全量下载-->");
            try {
                log.info("全量下载开始");
                log.info("请设置application.properties 文件中的sftp.allDownload=false关闭全量下载");
                downLoad(rootDirectory);
                log.info("全量下载结束");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                flag = false;
            }
        } else {
            log.info("未开启全量下载-->");
        }
    }

    private void downLoad(String directory) {
        try {
            sftpUtil.login();
            Vector<ChannelSftp.LsEntry> vector = sftpUtil.listFiles(directory);
            sftpUtil.logout();
            if (!vector.isEmpty() && vector.size() > 0) {
                Iterator<ChannelSftp.LsEntry> it = vector.iterator();
                while (it.hasNext()) {
                    ChannelSftp.LsEntry entry = it.next();
                    if (entry.getFilename().equals(".") || entry.getFilename().equals("..")) {
                        continue;
                    }
                    if (entry.getAttrs().isDir()) {
                        log.info("{} 是一个目录--->{}", entry.getLongname(), directory + File.separator + entry.getFilename());
                        downLoad(directory + File.separator + entry.getFilename());

                    } else {
                        log.info("{} 是一个文件", entry.getLongname());
                        sftpUtil.login();
                        File dayFile = new File(localDirectory + File.separator + "day");
                        if (!dayFile.exists()) {
                            dayFile.mkdirs();
                        }
                        File monthFile = new File(localDirectory + File.separator + "month");
                        if (!monthFile.exists()) {
                            monthFile.mkdirs();
                        }
                        if (entry.getFilename().indexOf("hechou") > 0) {
                            File file = new File(localDirectory + File.separator + "month" + File.separator + entry.getFilename());
                            if (!file.exists()) {
                                sftpUtil.download(directory, entry.getFilename(),
                                        localDirectory + File.separator + "month" + File.separator + entry.getFilename());
                            } else {
                                log.info("文件{}已经存在，不下载", file.getAbsolutePath());
                            }

                        } else if (entry.getFilename().indexOf("jihuo") > 0) {
                            File file = new File(localDirectory + File.separator + "month" + File.separator + entry.getFilename());
                            if (!file.exists()) {
                                sftpUtil.download(directory, entry.getFilename(),
                                        file.getAbsolutePath());
                            } else {
                                log.info("文件{}已经存在，不下载", file.getAbsolutePath());
                            }
                            sftpUtil.download(rootDirectory + File.separator + "jihuo", entry.getFilename(),
                                    localDirectory + File.separator + "month" + File.separator + entry.getFilename());
                        } else {
                            log.info(directory);
                            File file = new File(localDirectory + File.separator + "day" + File.separator + entry.getFilename());
                            if (!file.exists()) {
                                if (entry.getFilename().lastIndexOf(".xlsx") < 0) {
                                    log.info("不下载后缀为.xlsx的文件" + file.getAbsolutePath());
                                } else {
                                    sftpUtil.download(directory, entry.getFilename(),
                                            file.getAbsolutePath());
                                }
                            } else {

                                log.info("文件{}已经存在，不下载", file.getAbsolutePath());
                            }

                        }
                    }
                }
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sftpUtil.logout();
        }

    }
}
