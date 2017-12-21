$(function() {
    // 定义访问后台，获取头条列表以及一级类别列表的URL
    var imgUrl = '/o2o/frontend/getpersonimg';
    // 访问后台，获取头条列表以及一级类别列表
    $.getJSON(imgUrl, function(img) {
        if (img.success) {
            // 获取后台传递过来的头条列表
            var personImg = img.personImg;
            var rightpanel = '';
            rightpanel = '<img class="profile-img" src="' + personImg +'">';
            // 将拼接好的类别赋值给前端HTML控件进行展示
            $('.avatar').html(rightpanel);
        }
    });
});
