$(function () {
    var userName = '';
    getList();
    function getList() {
        //获取该店铺用户积分信息的URL
        var listUrl = '/o2o/shopadmin/listusershopmapsbyshop?pageIndex=1&pageSize=9999&userName='
        +userName;
        $.getJSON(listUrl,function (data) {
            if (data.success){
                var userShopMapList = data.userShopMapList;
                var tempHtml = '';
                //拼接成展示列表
                userShopMapList.map(function (item,index) {
                    tempHtml += '' + '<div class="row row-usershopcheck">'
                        + '<div class="col-50">' + item.user.name + '</div>'
                        + '<div class="col-50">' + item.point + '</div>'
                        + '</div>'
                });
                $('.usershopcheck-wrap').html(tempHtml);
            }
        });
    }

    $('#search').on('change',function (e) {
        //当在搜索框里输入信息的时候
        //依据输入的商品名模糊查询该商品的购买记录
        userName = e.target.value;
        //清空商品购买记录列表
        $('.usershopcheck-wrap').empty();
        //再次加载
        getList();
    });
});