{{*This VAR is unsafe*}}

        {{var myHTML}}
<span style="border: 1px solid red;">
    <strong>
        <i>Hello {{world}}!</i>
    </strong>
</span>
        {{/var}}

<div>myHTML = {{myHTML}}</div>