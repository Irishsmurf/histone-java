{{macro foo(a)}}
    {{var index = 0}}
    {{var lastIndex = a}}
    {{macro foo()}}
        {{if index < lastIndex}}
            {{index}}Hello world!
            {{var index = index + 1}}
            {{foo()}}
        {{/if}}
    {{/macro}}
    {{foo()}}
{{/macro}}

{{foo(5)}}