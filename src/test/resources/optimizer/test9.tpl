{{macro foo(a)}}
    {{for x in a}}
        {{x}}
    {{/for}}
{{/macro}}

{{foo([1, 2, 3])}}