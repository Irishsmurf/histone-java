{{*This template is unsafe*}}

        {{* load JSON object from file using loadJSON function *}}
        {{var url = resolveURI('examples/json.json', this.href)}}
        {{var data = loadJSON(url)}}

        {{macro dumpJSON(tree, level)}}
        {{var level = level or default_level}}
        {{for key:value in tree}}
<div style="padding-left: {{level * 20}}px;">
    {{if value.isMap()}}
    {{key}}: {
    {{dumpJSON(value, level + 1)}}
    }
    {{else}}
    {{key}}: {{value.toJSON()}}
    {{/if}}
</div>
        {{/for}}
        {{/macro}}

        JSON object from: <a target="_blank" href="{{url}}">
{{url}}
</a>
<div style="padding-left: 20px; padding-top: 10px;">
{{dumpJSON(data)}}
</div>