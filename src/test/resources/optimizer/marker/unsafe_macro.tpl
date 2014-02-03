{{*This MACRO CALL is unsafe*}}

        {{macro makeBorderAround(html)}}
<div style="border: 4px solid gray;">
    {{html5}}
</div>
        {{/macro}}

        {{makeBorderAround('<ul>
<li>this</li>
<li>is not</li>
<li>very convenient</li>')}}