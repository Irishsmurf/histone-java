{{* macro with fixed arguments list *}}
        {{macro makeList(a1, a2, a3)}}
<div>a1 = {{a1}}</div>
<div>a2 = {{a2}}</div>
<div>a3 = {{a3}}</div>
        {{/macro}}

        {{* call our macro *}}
        {{makeList('hello', 'world', 3.14*10/10+2)}}