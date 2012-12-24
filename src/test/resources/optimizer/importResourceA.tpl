{{import 'importResourceB.tpl'}}
{{macro importFromA()}}
    <subcontent>
        Imported from A
         {{importFromB()}}
    </subcontent>
{{/macro}}