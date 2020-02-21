var isPercentage = false;

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

function isNumber(val) {
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if (regPos.test(val) || regNeg.test(val)) {
        return true;
    } else {
        return false;
    }
}

function searchAndShow() {
    const dateStr = $('#date-input').val();
    $.ajax({
        type: 'GET',
        url: '/data/' + dateStr,
        data: {},
        dataType: 'json',
        success: function (data) {
            $('.data-tr').remove();
            $('.layui-form').remove();
            $('#count-span').text(data.length);
            var maKey = [];
            if (data.length !== 0) {
                var maObject = JSON.parse(data[0].ma.replace(/'/g, '"'));
                console.log(maKey);
                for (var key in maObject) {
                    $('.tr-head').append(
                        $('<th></th>').text(key).attr('class', 'data-tr')
                            .attr('lay-data', '{field:"' + key + '", sort:true, templet: "#tpl-' + key + '"}')
                    );
                    maKey.push(key);
                }
            }
            for (var a in data) {
                a = data[a];
                var maObject = JSON.parse(a.ma.replace(/'/g, '"'));
                var tr = $('<tr></tr>')
                    .attr('class', 'data-tr')
                    .append(
                        $('<td></td>').text(a.stockInfo.stockId)
                    ).append(
                        $('<td></td>').text(a.stockInfo.name)
                    ).append(
                        $('<td></td>').text(Number(a.turnOverRate) * 100)
                    );
                for (var key in maKey) {
                    var value = Number((maObject[maKey[key]] * 100).toFixed(2));
                    if (a.isPercentage) {
                        isPercentage = true;
                        value = Number((value * 100).toFixed(0));
                        //value = "" + value + "%";
                    }
                    var td = $('<td></td>').text(value);
                    tr.append(td);
                }
                $('tbody').append(tr);
            }
            layui.table.init('table', {
                limit: 99999
            });
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
            if (data.status) {
                alert('计算完成');
                location = location;
            }
        }
    });
});

// setInterval(function () {
//     $.each($('.layui-table-cell'), function (i, val) {
//         const text = $(this).text().replace('%', '');
//         if (isNumber(text) && text.length !== 6) {
//             const num = Number(text);
//             if (num > 0) {
//                 $(this).css('color', 'red');
//             } else if (num < 0) {
//                 $(this).css('color', 'green');
//             }
//
//             var newText = text;
//             if (newText.indexOf('.') === -1) {
//                 newText = newText;
//             }
//             if (isPercentage && text.indexOf('%') === -1) {
//                 $(this).text(text + '%');
//             }
//         }
//     });
// }, 100);
