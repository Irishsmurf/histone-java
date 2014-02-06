{{*This MACRO CALL is safe*}}


{{macro macro_2(bbb)}}
    {{bbb}}
{{/macro}}

{{macro makeBorderAround(html)}}
<div style="border: 4px solid gray;">
    {{html}}

    {{macro_2('aaaaaa')}}
</div>
{{/macro}}

{{makeBorderAround('<ul>')}}