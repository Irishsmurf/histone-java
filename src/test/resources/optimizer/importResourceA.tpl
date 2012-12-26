{{import 'importResourceB.tpl'}}

{{macro importFromA()}}
    Imported from A
    {{importFromB()}}
{{/macro}}