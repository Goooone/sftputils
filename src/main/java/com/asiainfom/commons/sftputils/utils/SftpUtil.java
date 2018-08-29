package com.asiainfom.commons.sftputils.utils;

import com.jcraft.jsch.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 *
 * @author king-pan
 * Date: 2018/8/24
 * Time: 上午10:59
 * Description: No Description
 */
@Data
@Slf4j
@Component
public class SftpUtil {
    private static final String CLZ_NAME = SftpUtil.class.getSimpleName() + ".";

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    @Value("${sftp.username}")
    private String username;
    /**
     * SFTP 登录密码
     */
    @Value("${sftp.password}")
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    @Value("${sftp.host}")
    private String host;
    /**
     * SFTP 端口
     */
    @Value("${sftp.port}")
    private int port;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SftpUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SftpUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SftpUtil() {
    }


    /**
     * 连接sftp服务器
     */
    public void login() throws Exception {
        JSch jsch = new JSch();
        if (privateKey != null) {
            // 设置私钥
            jsch.addIdentity(privateKey);
        }

        session = jsch.getSession(username, host, port);

        if (password != null) {
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //TODO 移除kerberos验证方法
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");

        session.setConfig(config);
        session.setTimeout(20000);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        sftp = (ChannelSftp) channel;

    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param path     上传到该目录
     * @param fileName sftp端文件名
     * @param input    输入流
     */
    public void upload(String path, String fileName, InputStream input) throws SftpException {
        try {
            sftp.cd(path);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) {
                    continue;
                }
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    sftp.mkdir(dir);
                    sftp.cd(dir);
                }
            }
        }
        //上传文件
        sftp.put(input, fileName);
    }


    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) {
        try {
            String filePath = directory + File.separator + downloadFile;
            boolean flag = false;
            try {
                //判断sftp上文件是否存在
                SftpATTRS attr = sftp.stat(filePath);
                flag = true;
            } catch (SftpException e1) {
                log.error("下载失败: 文件不存在" + directory + File.separator + downloadFile);
            }
            if (flag) {
                File file = new File(saveFile);
                sftp.get(filePath, new FileOutputStream(file));
            } else {
                log.info("文件不存在，等待下次下载-->{}", filePath);
            }
        } catch (Exception e) {
            log.error("下载失败:" + directory + File.separator + downloadFile, e);
        }

    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @param
     */
    public Vector<ChannelSftp.LsEntry> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    //上传文件测试
    public static void main(String[] args) throws Exception {
        SftpUtil sftp = new SftpUtil("ocdc", "nVZUMK#Qaz", "10.25.176.201", 22);
        sftp.login();
        File file = new File("/Users/king-pan/ls.txt");
        InputStream is = new FileInputStream(file);

        //sftp.upload("files/jihuo", "hubydcmcc_app_jihuo_201807.csv", is);
        //sftp.sftp.cd("files/2018-08-20");
        String saveFile = "/Users/king-pan/DDD1111/day/hubydcmcc_phone_detail_20180607.xlsx";
        sftp.download("files/2018-08", "hubydcmcc_phone_detail_20180823.xlsx", saveFile);
        //sftp.download("files/2018-08-20", "ls.txt", "/Users/king-pan/DDD1111/2018-08-20/ls.txt");
        is.close();
        sftp.logout();
    }
}
