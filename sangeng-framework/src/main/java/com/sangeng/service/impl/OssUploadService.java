package com.sangeng.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.sangeng.domain.ResponseResult;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.UploadService;
import com.sangeng.utils.PathUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@Data //生成getter和sertter方法，这里三个成员变量主要使用getter方法进行赋值
@ConfigurationProperties(prefix = "oss") //@ConfigurationProperties 的作用: 让JavaBean中属性值要和配置文件application.xml进行映射
public class OssUploadService implements UploadService {
    @Override
    public ResponseResult uploadImg(MultipartFile img) { //img是网络中传过来的流对象
        //判断文件类型
            //获取原始文件名
        String originalFilename = img.getOriginalFilename(); //得到上传时的文件名(本机需要上传的文件名，比如111.jpg上传到oss)
        //对原始文件名进行判断（只接受.png .jpg格式文件）
        if (!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")){
            //如果文件名 不是以 .png 结尾，抛出异常
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }
        //如果判断通过，则上传文件到OSS
//        根据当前文件名，生成一个存放路径 2023/6.14/uuid.png  或.jpg
        String filePath = PathUtils.generateFilePath(originalFilename);// 2022/1/15/+uuid+.jpg 得到文件路径
        String url = uploadOss(img,filePath); //上传文件至OSS后返回一个外链（可以通过此外链url 访问OSS中上传的图片）

        return ResponseResult.okResult(url); //返回结果data是外链 值
    }

    private String accessKey;
    private String secretKey;
    private String bucket;

    private String uploadOss(MultipartFile imgFile, String filePath){
//        注：用七牛云的oss，所以导包的时候 要导入七牛云的包。     用人家的代码，导人家的包 com.qiniu.storage
        //构造一个带指定 Region 对象的配置类
//修改1.Region指定数据存储区域，autoRegion()自动根据七牛云账号找到选的区域（我选的是 华北）
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传

////   注：为了安全起见，AK,SK,bucket存储空间名，都是从application.xml 配置文件中读取到的
////修改2.复制七牛云官网-个人中心-密钥管理-  AK和SK
//        String accessKey = "";
//        String secretKey = "";
////修改3.创建存储空间的名字 pk-sg-blog
//        String bucket = "";

//默认不指定key的情况下，以文件内容的hash值作为文件名,  比如上传一张图片，名字为hash值生成的名字
//修改4.指定上传文件到oss时，文件的存储名
        String key = filePath; // 指定生成路径
        try {
//修改5 注释掉默认上传，改成 前端传过来的文件流
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
//上传文件——前端传过来的图片
//            获取到网络中传过来的流对象的inputstream
            InputStream inputStream = imgFile.getInputStream();//前端传过来的文件转成inputstream流

            Auth auth = Auth.create(accessKey, secretKey);//创建凭证
            String upToken = auth.uploadToken(bucket); //上传凭证
            try {
//修改6 put方法 第一个参数 要放上面 自己定义的 inputStream对象
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key); //111.png  key值就是上传后的 文件名
                System.out.println(putRet.hash); //Fo2AVLRHugoNbek6XZ8Uy-DCnuSL
//              外链 http://rw7y62wqd.hb-bkt.clouddn.com/111.png
//                外链= http://+ 测试域名+文件名               七牛云测试域名，免费用30天，过期回收域名
                return "http://rw7y62wqd.hb-bkt.clouddn.com/"+key;   //上传成功，返回一个对应的外链
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore  异常类型 改大一点
        }
        return "www";
    }

}
