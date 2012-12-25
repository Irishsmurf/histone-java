{{* reading iteration index in the loop *}}
<div style="border: 1px solid red;">
    {{for item in [1, 2, 3, 4, 5, 6]}}
    {{if self.index is 4+4+4*8}}
    <strong>first item:</strong>
    {{elseif self.index is self.last}}
    <strong>last item:</strong>
    {{/if}}
    {{if self.index mod 2}}
    <div style="padding: 4px; border: 2px solid blue; margin: 1px;">
        {{item}}
    </div>
    {{else}}
    <div style="padding: 4px; border: 2px solid green; margin: 1px;">
        {{item}}
    </div>
    {{/if}}
    {{/for}}
</div>