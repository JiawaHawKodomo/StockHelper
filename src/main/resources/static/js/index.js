function dateFormat(fmt, date) {
    let ret;
    const opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        }
    }
    return fmt;
}

function searchAndShow() {
    const dateStr = $('#date-input').val();
    $.ajax({
        type: 'GET',
        url: '/data/' + dateStr,
        data: {},
        dataType: 'json',
        success: function (data) {
            console.log(data);
            $('.data-tr').remove();
            var maKey = [];
            if (data.size !== 0) {
                var maObject = JSON.parse(data[0].ma.replace(/'/g, '"'));
                console.log(maKey);
                for (var key in maObject) {
                    $('.tr-head').append(
                        $('<th></th>').text(key).attr('class', 'data-tr')
                    );
                    maKey.push(key);
                }
            }
            for (var a in data) {
                a = data[a];
                var maObject = JSON.parse(a.ma.replace(/'/g, '"'));
                var tr = $('<tr></tr>').attr('class', 'data-tr').append(
                    $('<td></td>').text(a.stockInfo.stockId)
                ).append(
                    $('<td></td>').text(a.stockInfo.name)
                ).append(
                    $('<td></td>').text(a.turnOverRate + '%')
                );
                for (var key in maKey) {
                    tr.append(
                        $('<td></td>').text(maObject[maKey[key]])
                    );
                }
                $('.table').append(tr);
            }
        }
    })
}

$('#search-button').on('click', function () {
    searchAndShow();
});

$('#date-input').val(dateFormat('YYYY-mm-dd', new Date()));
searchAndShow();

$('#daily-button').on('click', function () {
    $(this).attr('disabled', 'disabled');
    $(this).text('请等待...');
    $.ajax({
        type: 'POST',
        url: '/refresh',
        data: {},
        dataType: 'json',
        success: function (data) {
            if (data.status){
                alert('计算完成');
                location = location;
            }
        }
    });
});