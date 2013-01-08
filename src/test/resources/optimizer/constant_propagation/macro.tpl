{{var a = "Hello"}}

        {{macro f1}}
        {{var b = "world"}}
        {{a}},{{b}}
        {{/macro}}

        {{macro f2}}
        {{f1()}}
        {{/macro}}