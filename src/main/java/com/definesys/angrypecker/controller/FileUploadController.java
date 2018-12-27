package com.definesys.angrypecker.controller;//package com.definesys.dragon.controller;
//
//import com.definesys.dragon.properties.DragonProperties;
//import com.definesys.mpaas.common.exception.MpaasRuntimeException;
//import com.definesys.mpaas.common.http.Response;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Controller
//@RequestMapping("/api/util")
//public class FileUploadController {
//    //上传路径
//    @Autowired
//    DragonProperties dragonProperties;
//
//    @Value("${com.define.dragon.file.urlprefix}")
//    private String urlprefix;
//
//
//    @RequestMapping(value = "/testUploadFile", method = RequestMethod.POST)
//    public void testUploadFile(HttpServletRequest req,
//                               MultipartHttpServletRequest multiReq) {
//        // 获取上传文件的路径
//        String uploadFilePath = multiReq.getFile("file1").getOriginalFilename();
//        System.out.println("uploadFlePath:" + uploadFilePath);
//        // 截取上传文件的文件名
//        String uploadFileName = uploadFilePath.substring(
//                uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
//        System.out.println("multiReq.getFileList()" + uploadFileName);
//        // 截取上传文件的后缀
//        String uploadFileSuffix = uploadFilePath.substring(
//                uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
//        System.out.println("uploadFileSuffix:" + uploadFileSuffix);
//        FileOutputStream fos = null;
//        FileInputStream fis = null;
//        try {
//            fis = (FileInputStream) multiReq.getFile("file1").getInputStream();
//            fos = new FileOutputStream(new File(".//uploadFiles//" + uploadFileName
//                    + ".")
//                    + uploadFileSuffix);
//            byte[] temp = new byte[1024];
//            int i = fis.read(temp);
//            while (i != -1){
//                fos.write(temp,0,temp.length);
//                fos.flush();
//                i = fis.read(temp);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 本地上传文件接口
//     * @param
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    @CrossOrigin
//    @PostMapping("/upload")
//    @ResponseBody
//    public Response upload(@RequestParam("file") MultipartFile[] files,
//                           HttpServletRequest request) throws IOException {
//        if (null != files && files.length > 0) {
//            //遍历并保存文件
//            for (MultipartFile file : files) {
//                if (files != null) {
//                    //取得当前上传文件的文件名称
//                    String fileName =  file.getOriginalFilename();
//                    byte[] bytes=file.getBytes();
//                    Path path = Paths.get(dragonProperties.getFile().getUploadPath() + fileName);
//                    try {
//                        Files.write(path,bytes);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return Response.error("异常");
//                    }
//                    return Response.ok().setMessage(urlprefix + "/files/" + fileName);
//                }
//            }
//        } else {
//            return Response.error("上传文件不能为空");
//        }
//        return Response.ok();
//    }
//
//
//
//
//}
