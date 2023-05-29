package xyz.lnpbqc.webchat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.lnpbqc.webchat.Utils.JsonUtils;
import xyz.lnpbqc.webchat.config.ServerInfo;
import xyz.lnpbqc.webchat.pojo.JsonResult;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

@RestController
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

//    拿到所有的emotion
    @GetMapping("/getAllEmos")
    public ArrayList<String> getAllEmos(){
        logger.info("正在获取emoji");
        String path = ImageController.class.getClassLoader().getResource("static/").getPath();
        File[] emos = new File(path+"emotions/").listFiles();
        ArrayList<String> strings = new ArrayList<>();
        for(File e:emos){
            strings.add("/api/getImage?type=emo&name="+e.getName()+"&user=");
        }
        return strings;
    }

//获得图片
    @GetMapping("/getImage")
    public void getImage(String type, String name, String user, HttpServletResponse response){
//        type为img为图片，为emo为表情包
        try{
            File userFile = null;
            if(type.equals("img")){
                logger.info(user+"正在获取图片"+name);
                String path = ImageController.class.getClassLoader().getResource("static/").getPath();
                userFile = new File(path+"images/" + user + "/");
            }else if(type.equals("emo")){
                String path = ImageController.class.getClassLoader().getResource("static/").getPath();
                userFile = new File(path+"emotions/");
            }
            String path = userFile.getAbsolutePath();
            String imgType = ImageIO.getImageReaders(ImageIO.createImageInputStream(new FileInputStream(path+"/"+name))).next().getFormatName();
            response.setContentType("image/"+imgType);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedImage originalImage = ImageIO.read(new File(path +"/"+ name));

            // 计算新的图片大小
            int newWidth = type.equals("img")?300:30;
            int newHeight = (int) Math.round(originalImage.getHeight() * (newWidth / (double) originalImage.getWidth()));

            // 创建新的缩放后的图片
            BufferedImage resizedImage = new ImageUtils().resizeImage(originalImage,newWidth,newHeight);
            ImageIO.write(resizedImage,"png",outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PostMapping("/uploadImage")
    public String uploadImage(String user, String key, MultipartFile file){
        logger.info(user+"上传了"+file.getOriginalFilename());
        boolean flag = true;
        try{
            if(!ServerInfo.getLegal(user).equals(key)){
                flag=false;
                throw new RuntimeException("非法用户");
            }
            String path = ImageController.class.getClassLoader().getResource("static/").getPath();
            File userFile = new File(path+"images/" + user + "/");
            if (!userFile.exists()){
                userFile.mkdirs();
            }

            InputStream inputStream = file.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(userFile.getAbsoluteFile()+"/" + file.getOriginalFilename());
            byte cont[] = new byte[1024];
            int len;
            while((len = inputStream.read(cont))!=-1){
                fileOutputStream.write(cont,0,len);
            }
            inputStream.close();
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                return flag?JsonUtils.toJson(new JsonResult("err","y")):"";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ImageUtils{
        public BufferedImage resizeImage(BufferedImage origin, int targetWidth, int targetHeight){
            Image result = origin.getScaledInstance(targetWidth,targetHeight,Image.SCALE_AREA_AVERAGING);
            BufferedImage output = new BufferedImage(targetWidth,targetHeight,BufferedImage.TYPE_INT_ARGB);
            // 将原始图片复制到带有透明背景的 BufferedImage 中
            Graphics2D g = output.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(result, 0, 0, null);
            g.dispose();

//            output.getGraphics().drawImage(result,0,0,null);
            return output;
        }
    }
}
