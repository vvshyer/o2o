package com.sun.o2o.entity;

import java.util.Date;

/**
 * 顾客消费的商品映射
 */
public class ProductSellDaily {

    //主键Id
    private Integer productSellDailyId;
    //销量的日期，精确到天
    private Date createTime;
    //销量
    private Integer total;
    //商品信息实体类
    private Product product;
    //店铺信息实体类
    private Shop shop;

    public Integer getProductSellDailyId() {
        return productSellDailyId;
    }

    public void setProductSellDailyId(Integer productSellDailyId) {
        this.productSellDailyId = productSellDailyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
