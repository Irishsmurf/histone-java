{{macro m(x)}}
    {{var a = 0}}
    {{for i in x}}
        {{var a = a + 2}}
    {{/for}}
    {{a}}
{{/macro}}