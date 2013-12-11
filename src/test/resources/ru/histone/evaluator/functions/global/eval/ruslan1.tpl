{{* Переданный при рендеринге шаблона this = 'THIS' *}}

{{var PI = 3.14}}

{{macro hello(name)}}Hello {{name}}!{{/macro}}

{{eval('PI = {{PI}}, {{hello("world")}}, {{this}}')}}