$(function() {
    var loading = false;
    // 分页允许返回的最大条数，超过此数则禁止访问后台
    var maxItems = 20;
    // 一页返回的最大条数
    var pageSize = 10;
    // 获取奖品列表的URL
    var listUrl = '/o2o/frontend/listuserawardmapsbycustomer';
    // 页码
    var pageNum = 1;
    var awardName = '';
    addItems(pageSize, pageNum);

    /**
     * 获取分页展示的奖品兑换记录列表
     *
     * @param pageSize
     * @param pageIndex
     * @returns
     */
    function addItems(pageSize, pageIndex) {
        // 拼接出查询的URL，赋空值默认就去掉这个条件的限制，有值就代表按这个条件去查询
        var url = listUrl + '?pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&awardName=' + awardName;
        // 设定加载符，若还在后台取数据则不能再次访问后台，避免多次重复加载
        loading = true;
        // 访问后台获取相应查询条件下的店铺列表
        $.getJSON(url, function(data) {
            if (data.success) {
                // 获取总数
                maxItems = data.count;
                var html = '';
                // 遍历店铺列表，拼接出卡片集合
                data.userAwardMapList.map(function(item, index) {
                    var status = '';
                    //根据usedStatus现实是否已在实体店领取过奖品
                    if(item.usedStatus == 0){
                        status = "未领取";
                    }else if(item.usedStatus == 1){
                        status = "已领取";
                    }
                    html += '' + '<div class="card" data-user-award-id="'
                        + item.userAwardId + '">'
                        + '<div class="card-header">'+ item.shop.shopName
                        + '<span class="pull-right">' + status
                        + '</span></div>' + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.award.awardName
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.createTime).Format("yyyy-MM-dd")
                        + '</p>' + '<span>消耗积分:' + item.point + '</span>'
                        + '</div>' + '</div>';
                });
                // 将卡片集合添加到目标HTML组件里
                $('.list-div').append(html);
                // 获取目前为止已显示的卡片总数，包含之前已经加载的
                var total = $('.list-div .card').length;
                // 若总数达到跟按照此查询条件列出来的总数一致，则停止后台的加载
                if (total >= maxItems) {
                    //加载完毕，则注销无限加载时间
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    //删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                    return;
                }
                // 否则页码加1，继续load出新的奖品
                pageNum += 1;
                // 加载结束，可以再次加载了
                loading = false;
                // 刷新页面，显示新加载的奖品
                $.refreshScroller();
            }
        });
    }

    $('.list-div').on(
        'click',
        '.card',
        function (e) {
            var userAwardId = e.currentTarget.dataset.userAwardId;
            window.location.href = '/o2o/frontend/myawarddetail?userAwardId=' + userAwardId;
        }
    );

    // 下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });


    // 搜索查询条件按照奖品名模糊查询
    $('#search').on('change', function(e) {
        awardName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    // 点击后打开右侧栏
    $('#me').click(function() {
        $.openPanel('#panel-right-demo');
    });

    // 初始化页面
    $.init();
});
