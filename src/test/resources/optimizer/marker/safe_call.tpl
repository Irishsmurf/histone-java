{{*This CALL is safe*}}

        {{macro makeBorderAround(html)}}
<div style="border: 4px solid gray;">
    {{html}}
</div>
        {{/macro}}

        {{makeBorderAround('<ul>
<li>this</li>
<li>is not</li>
<li>very convenient</li>')}}