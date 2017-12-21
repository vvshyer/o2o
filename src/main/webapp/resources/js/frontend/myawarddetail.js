$(function() {
    // 从地址栏的URL里获取productId
    var userAwardId = getQueryString('userAwardId');
    // 获取奖品信息的URL
    var awardUrl = '/o2o/frontend/getawardbyuserawardid?userAwardId='
        + userAwardId;
    // 访问后台获取该奖品的信息并渲染
    $.getJSON(awardUrl, function(data) {
        if (data.success) {
            // 获取奖品信息
            var award = data.award;
            // 给奖品信息相关HTML控件赋值

            // 奖品缩略图
            $('#award-img').attr('src', getContextPath()+award.awardImg);
            // 奖品更新时间
            $('#create-time').text(
                new Date(data.userAwardMap.createTime).Format("yyyy-MM-dd"));
            // 奖品名称
            $('#award-name').text(award.awardName);
            // 奖品简介
            $('#award-desc').text(award.awardDesc);
            var imgListHtml = '';
            if (data.usedStatus==0) {
                imgListHtml += '<div> <img src="/o2o/frontend/generateqrcode4award?userAwardId='
                    + userAwardId
                    + '" width="100%"/></div>';
            }
            $('#imgList').html(imgListHtml);
        }
    });
    // 点击后打开右侧栏
    $('#me').click(function() {
        $.openPanel('#panel-right-demo');
    });
    $.init();
});
