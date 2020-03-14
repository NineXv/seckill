var update = {
    detail : {
        // 详情页初始化
        init : function(params) {
            var killPhone = $.cookie('killPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get('/seckill/time/now', {}, function(result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    // 时间判断，计时交互
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log(result['reult:'] + result);
                }
            });
        }
    }
}