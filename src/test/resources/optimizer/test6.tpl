{{macro byteFormat(bytes)}}
        {{var s = ['B', 'Kb', 'MB', 'GB', 'TB', 'PB']}}
        {{var g = 2+2}}
        {{var e = (bytes.log() / 1024.log()).floor()}}
        {{(bytes / 1024.pow(e)).toFixed(2) + s[e]}}
        {{/macro}}

        {{for power in range(1, 5)}}
<div>
    {{var bytes = 1094.pow(power)}}
    {{bytes}} bytes = {{byteFormat(bytes)}}


</div>
        {{/for}}