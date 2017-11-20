$(function() {
    var loading = false;
    // 分页允许返回的最大条数，超过此数则禁止访问后台
    var maxItems = 999;
    // 一页返回的最大条数
    var pageSize = 10;
    // 获取奖品列表的URL
    var listUrl = '/o2o/frontend/listawardsbyshop';
    // 兑换奖品的URL
    var exchangeUrl = '/o2o/frontend/adduserawardmap';
    // 页码
    var pageNum = 1;
    // 从地址栏URL里尝试获取parent shop category id.
    var shopId = getQueryString('shopId');
    var awardName = '';
    var canProceed = false;
    var totalPoint = 0;
    // 预先加载10条店铺信息
    addItems(pageSize, pageNum);

    /**
     * 获取分页展示的店铺列表信息
     *
     * @param pageSize
     * @param pageIndex
     * @returns
     */
    function addItems(pageSize, pageIndex) {
        // 拼接出查询的URL，赋空值默认就去掉这个条件的限制，有值就代表按这个条件去查询
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&shopId=' + shopId + '&awardName=' + awardName;
        // 设定加载符，若还在后台取数据则不能再次访问后台，避免多次重复加载
        loading = true;
        // 访问后台获取相应查询条件下的店铺列表
        $.getJSON(url, function(data) {
            if (data.success) {
                // 获取当前查询条件下店铺的总数
                maxItems = data.count;
                var html = '';
                // 遍历店铺列表，拼接出卡片集合
                data.awardList.map(function(item, index) {
                    html += '' + '<div class="card" data-award-id="'
                        + item.awardId + '" data-point="' + item.point
                        + '">' + '<div class="card-header">'
                        + item.awardName + '<span class="pull-right">需要积分'
                        + item.point + '</span></div>'
                        + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + getContextPath() + item.awardImg
                        + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">'
                        + item.awardDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                        + '更新</p>';
                    if(data.totalPoint!=undefined){
                        //若用户有积分，则显示领取按钮
                        html += '<span>点击领取</span></div></div>'
                    }else {
                        html += '</div></div>'
                    }
                });
                // 将卡片集合添加到目标HTML组件里
                $('.list-div').append(html);
                if (data.totalPoint != undefined){
                    //若用户在该店铺有几分，则显示
                    canProceed = true;
                    $("#title").text('当前积分' + data.totalPoint);
                    totalPoint = data.totalPoint;
                }
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

    // 下滑屏幕自动进行分页搜索
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    $('.award-list').on('click', '.card', function(e) {
        //若用户在该店铺有积分并且积分数大于该奖品需要消耗的积分
        if(canProceed && (totalPoint >= e.currentTarget.dataset.point)){
            //则弹出操作确认框
            $.confirm(
                '需要消耗' + e.currentTarget.dataset.point + '积分，确定操作么？',
                function () {
                    //访问后台，领取奖品
                    $.ajax({
                        url:exchangeUrl,
                        type:'POST',
                        data:{
                            awardId:e.currentTarget.dataset.awardId
                        },
                        dataType:'json',
                        success:function (data) {
                            if (data.success){
                                $.toast('操作成功！');
                                totalPoint = totalPoint - e.currentTarget.dataset.point;
                                $("#title").text(
                                    '当前积分' + totalPoint
                                );
                            }else {
                                $.toast('操作失败！');
                            }
                        }
                    });
                }
            );
        }else {
            $.toast('积分不足或无权操作！');
        }
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
