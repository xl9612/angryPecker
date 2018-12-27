package com.definesys.angrypecker.util.common;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.UUID;

public class FileUploadAndDownloadUtil {

    /**
     * 图片上传
     * @throws IOException
     */
    public static String uploadPicture(MultipartFile file, String fileName)
            throws IOException{
        String suffix =".jpg";
        String fileNameArray[] = fileName.split("\\.");
        if (fileNameArray.length>1){
            suffix = fileNameArray[1];
        }
        String filePath ="/resource/";
        fileName = UUID.randomUUID()+"."+suffix;
        // 设置文件存储路径

        File dest = new File( filePath+ fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return fileName;
    }

    /**
     * 图片查找
     * @param fileId
     */
    public static byte[] getPictureByteArray(String fileId){
        // 根据fileId扫描指定文件夹，拿到指定文件
        File searchedFile = FileUploadAndDownloadUtil.searchFile("/resource/",fileId);
        if(searchedFile==null){
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(searchedFile);
            int length = fileInputStream.available();
            byte[] resultData = new byte[length];
            fileInputStream.read(resultData);
            fileInputStream.close();
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查找文件
     */
    public static File searchFile(String rootFolderName,String fileId){
        File rootFolder = new File(rootFolderName);
        if(!rootFolder.exists()||!rootFolder.isDirectory()){
            return null;
        }
        File[] totalFile = rootFolder.listFiles();
        for(File item : totalFile){
            if(item.getName().equals(fileId)){
                return item;
            }
        }
        return null;
    }

    /**
     * 上传到本工程下面
     * @param file
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    public static void uploadFile(MultipartFile file, String filePath, String fileName)
            throws IOException{

        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }
        // 设置文件存储路径
        // 设置文件存储路径
        File dest = new File(filePath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream(dest));

        out.write(file.getBytes());
        out.flush();
        out.close();
    }

    /**
     * 文件下载
     * @param response
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    public static void downloadFile(HttpServletResponse response, String filePath,
                                    String fileName)throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File file = new File(filePath, fileName);
        //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)
        // 然后下载到C:\\users\\downloads即本机的默认下载的目录
        if (file.exists()) {
            System.out.println(filePath+"|"+fileName+"|"+file.getName());
            response.setContentType("application/force-download");// 设置强制下载不打开
            // 设置文件名
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }

            //释放资源
            if (bis != null) {
                bis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }else {
            throw new IOException("该文件不存在");
        }
    }

}
