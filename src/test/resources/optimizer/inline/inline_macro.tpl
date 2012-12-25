{{*In result AST we should have no Macro (but it should exists in optimizer context)*}}

        {{macro makeBorderAround(html)}}
<div style="border: 4px solid gray;">
    {{html}}
</div>
        {{/macro}}