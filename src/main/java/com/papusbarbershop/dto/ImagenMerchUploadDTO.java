package com.papusbarbershop.dto;

public class ImagenMerchUploadDTO {
    private String s3Key;
    private String url;
    private Integer orden;

    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
