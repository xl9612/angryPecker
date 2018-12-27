package com.definesys.angrypecker.controller;

import com.definesys.angrypecker.pojo.DragonProjects;
import com.definesys.angrypecker.pojo.DragonUser;
import com.definesys.angrypecker.service.DragonUserService;
import com.definesys.angrypecker.util.common.FileUploadAndDownloadUtil;
import com.definesys.mpaas.common.http.Response;
import com.definesys.mpaas.log.SWordLogger;
import com.definesys.mpaas.query.MpaasQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private MpaasQueryFactory sw;

    @Autowired
    private SWordLogger logger;

    @Autowired
    private DragonUserService dragonUserService;

     /**
     * 上传用户头像
     * @param file
     * @return
     */
    @RequestMapping(value = "uploadUserIcon", method = RequestMethod.POST)
    @ResponseBody
    public Response uploadUserIcon(@RequestParam("file") MultipartFile file) {
        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            try {
                String fileUrl = FileUploadAndDownloadUtil.uploadPicture(file,fileName);
                DragonUser dragonUser = dragonUserService.getDragonUser();
                sw.buildQuery()
                        .update("user_icon",fileUrl)
                        .eq("id",dragonUser.getId())
                        .doUpdate(DragonUser.class);
                return  Response.ok().set("fileUrl",fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return Response.error("用户头像上传失败:"+e.getMessage());
            }
        }
        return Response.error("用户头像为空");
    }

    /**
     * 上传项目logo接口
     * @param file
     * @param rowId
     * @return
     */
    @RequestMapping(value = "uploadProjectIcon", method = RequestMethod.POST)
    @ResponseBody
    public Response uploadProjectIcon(@RequestParam("file") MultipartFile file, String rowId) {
        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            try {
                if(rowId==null||rowId.isEmpty()){
                    String fileUrl = FileUploadAndDownloadUtil.uploadPicture(file,fileName);
                    return Response.ok().set("fileUrl",fileUrl);
                }
                String fileUrl = FileUploadAndDownloadUtil.uploadPicture(file,fileName);
                sw.buildQuery()
                        .update("project_logo",fileUrl)
                        .addRowIdClause("id","=",rowId)
                        .doUpdate(DragonProjects.class);
                return  Response.ok().set("fileUrl",fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return Response.error("项目logo上传失败:"+e.getMessage());
            }
        }
        return Response.error("项目logo为空");
    }

    /**
     * 上传task图片文件接口
     * @param file
     */
    @RequestMapping(value = "/uploadTaskFile")
    @ResponseBody
    public Response uploadTaskFile(@RequestParam("file") MultipartFile file){
        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            try {
                String fileUrl = FileUploadAndDownloadUtil.uploadPicture(file,fileName);
                return Response.ok().set("fileUrl",fileUrl).set("fileName",fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.error("用户头像上传失败:"+e.getMessage());
            }
        }
        return Response.error("用户头像为空");
    }

    /**
     * 图片下载接口
     *
     */
    @RequestMapping(value = "/getPicture/{fileId}")
    public HttpServletResponse getPicture(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        //从util获取byte数组，放入到http response中
        String suffix = "*";
        String [] fileIdArray = fileId.split("\\.");
        if (fileIdArray.length>1){
            suffix = fileIdArray[1];
            if (suffix!=null&&suffix.equals("jpg")){
                suffix = "jpeg";
            }
        }
        response.setContentType("image/"+suffix+";charset=UTF-8");
        response.setHeader("Content-Disposition","inline;filename=\""+fileId+"\"");
        response.setHeader("Cache-Control","public");
        response.setHeader("Pragma","Pragma");
        response.setDateHeader("expires",System.currentTimeMillis() + 1000 * 60*60);
        byte[] data = FileUploadAndDownloadUtil.getPictureByteArray(fileId);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
        return response;
    }


    /**
     * 保存文件到本地文件夹
     */
    /*@RequestMapping(value = "upload")
    @ResponseBody
    public Response upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

         if (file.isEmpty()) {
             return Response.error("文件为空");
         }
         // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);

        // 获取文件的后缀名,比如图片的jpeg,png
//            String suffixName = fileName.substring(fileName.lastIndexOf("."));
//            System.out.println("上传的后缀名为：" + suffixName);

        // 文件上传后的路径
        fileName = UUID.randomUUID() + ".jpg";
        System.out.println("转换后的名称:"+fileName);
        String filePath = "C:/work/zhong/dragon/src/main/resources/resources/test/00/";
        File dest = new File(filePath + fileName);

        try {
            System.out.println(dest.getParentFile());
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            //上传成功
            return Response.ok().setMessage("上传成功");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
          //上传失败
        return  Response.error( "fail to save ");
    }*/
}
