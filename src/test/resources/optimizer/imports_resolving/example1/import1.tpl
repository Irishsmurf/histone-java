{{var PREFIX='Hello'}}

{{import 'subimport1.tpl'}}

{{macro foo(t)}}
{{indent()}}{{PREFIX}}, {{t}}
{{/macro}}